package de.twometer.amogus.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import de.twometer.amogus.concurrency.AsyncScheduler
import de.twometer.amogus.model.*
import de.twometer.amogus.net.*
import de.twometer.neko.util.MathF
import de.twometer.neko.util.MathF.PI
import mu.KotlinLogging
import org.joml.Vector3f
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {}

object AmongUsServer : Server() {

    private val sessions = ConcurrentHashMap<String, ServerSession>()
    private val playerIdCounter = AtomicInteger()
    private const val disableVictory = false
    private val scheduler = AsyncScheduler()

    fun main() {
        registerAllNetMessages(kryo)
        start()
        bind(32783)

        addListener(object : Listener() {
            override fun received(conn: Connection, msg: Any) {
                handle(conn as PlayerClient, msg)
            }

            override fun connected(conn: Connection) {
                logger.info { "Client connected from ${conn.remoteAddressTCP}" }
            }

            override fun disconnected(conn: Connection) {
                val client = conn as PlayerClient
                logger.info { "Player #${client.id} disconnected" }
                val session = client.session ?: return

                session.players.remove(client)
                if (session.players.size == 0) {
                    sessions.remove(session.code)
                    logger.debug { "Session ${session.code} is now empty and was destroyed" }
                    return
                }

                if (client.isHost) {
                    logger.debug { "Finding new host for session ${session.code}" }
                    val newHost = session.players.filter { !it.isHost }.shuffled().first()
                    logger.debug { "Selected host: ${newHost.id}" }
                    session.host = newHost.id
                    session.broadcast(OnSessionUpdate(session.code, session.host, session.config))
                }

                session.broadcast(OnPlayerLeave(client.id))

                checkVictory(session)
            }
        })

        logger.info { "Server started and bound" }

        scheduler.start()
    }

    override fun newConnection(): Connection {
        return PlayerClient()
    }

    // God function lets go
    private fun handle(client: PlayerClient, msg: Any) {
        when (msg) {
            is HandshakeRequest -> {
                client.player.id = playerIdCounter.incrementAndGet()
                client.sendTCP(HandshakeResponse(msg.version == 2, client.player.id))
                logger.info { "Player #${client.id} connected" }
            }
            is SessionCreateRequest -> {
                val code = CodeGenerator.newSessionCode()
                val session = ServerSession(code, client.id)
                sessions[code] = session
                client.sendTCP(SessionCreateResponse(true, code))
                logger.info { "Player ${client.id} created session ${session.code}" }
            }
            is SessionJoinRequest -> {
                val session = sessions[msg.code]
                if (session == null)
                    client.sendTCP(SessionJoinResponse(false, "Session does not exist"))
                else if (session.players.any { it.username.equals(msg.username, true) })
                    client.sendTCP(SessionJoinResponse(false, "Username is taken"))
                else if (msg.username.isBlank())
                    client.sendTCP(SessionJoinResponse(false, "Invalid username"))
                else if (session.isFull)
                    client.sendTCP(SessionJoinResponse(false, "Lobby full"))
                else {
                    logger.info { "Player ${client.id} joining session ${session.code}" }
                    client.session = session
                    client.player.username = msg.username
                    client.player.role = PlayerRole.Crewmate
                    client.player.color = session.randomColor()
                    client.sendTCP(SessionJoinResponse(true))
                    session.players.forEach {
                        client.sendTCP(OnPlayerJoin(it.id))
                        client.sendTCP(OnPlayerUpdate(it.id, it.username, it.color, it.role))
                    }
                    client.sendTCP(OnSessionUpdate(session.code, session.host, session.config))
                    session.players.add(client)
                    session.broadcast(OnPlayerJoin(client.id))
                    session.broadcast(OnPlayerUpdate(client.id, client.username, client.color, client.role))
                }
            }
            is SessionConfigureRequest -> {
                if (!client.isHost) return
                logger.info { "Player ${client.id} reconfiguring their session" }
                val session = client.session!!
                session.config = msg.config
                client.sendTCP(SessionConfigureResponse(true))
                session.broadcast(OnSessionUpdate(session.code, session.host, session.config))
            }
            is ColorChangeRequest -> {
                if (!client.inSession) return
                logger.info { "Player ${client.id} attempting to change their color from ${client.color} to ${msg.newColor}" }
                if (client.session!!.players.any { c -> c.color == msg.newColor })
                    client.sendTCP(ColorChangeResponse(false))
                else {
                    client.player.color = msg.newColor
                    client.session!!.broadcast(OnPlayerUpdate(client.id, client.username, client.color, client.role))
                    client.sendTCP(ColorChangeResponse(true))
                }
            }
            is CastVote -> {
                if (!client.inSession) return
                logger.info { "Player ${client.id} casting vote for ${msg.playerId}" }
                client.session!!.broadcast(OnPlayerVoted(client.id, msg.playerId))
                client.session!!.votes[client.id] = msg.playerId
            }
            is KillPlayer -> {
                if (!client.inSession) return
                if (client.role != PlayerRole.Impostor) return
                logger.info { "Impostor ${client.id} killing player ${msg.playerId}" }
                client.session!!.findPlayer(msg.playerId)?.run {
                    player.state = PlayerState.Ghost
                    client.session!!.broadcast(OnPlayerKilled(this.id))
                    checkVictory(client.session!!)
                }
            }
            is StartGame -> {
                if (!client.isHost) return
                logger.info { "Player ${client.id} starting their game" }
                val session = client.session!!
                // ReZero
                resetSession(session)
                // Select impostors
                repeat(session.config.impostorCount) {
                    selectImpostor(session)?.player?.role = PlayerRole.Impostor
                }
                // Gigasync
                session.broadcast(OnSessionUpdate(session.code, session.host, session.config))
                session.players.forEach {
                    session.broadcast(OnPlayerUpdate(it.id, it.username, it.color, it.role))
                }
                assignSpawnPositions(session)
                // And lezzgooooooooo :3
                val commonTask = TaskGenerator.newCommonTask()
                session.players.forEach {
                    val tasks = ArrayList<PlayerTask>()
                    tasks.add(commonTask)
                    repeat(session.config.shortTasks) { tasks.add(TaskGenerator.newShortTask()) }
                    repeat(session.config.longTasks) { tasks.add(TaskGenerator.newLongTask()) }
                    tasks.shuffle()
                    if (it.role != PlayerRole.Impostor) session.totalTaskStages += tasks.sumOf { task -> task.length }
                    it.sendTCP(OnGameStarted(tasks))
                }
            }
            is SabotageStart -> {
                val session = client.session ?: return
                if (client.role != PlayerRole.Impostor) return
                synchronized(session.sabotageLock) {
                    if (session.currentSabotage != null) return
                    session.currentSabotage = msg.sabotage
                    logger.info { "Impostor ${client.id} sabotaging ${msg.sabotage}" }
                    session.broadcast(OnSabotageChanged(msg.sabotage, CodeGenerator.newO2Code()))
                    if (msg.sabotage.critical) {
                        logger.debug { "Starting kill task" }
                        session.sabotageKillTask = scheduler.runLater(45000) {
                            logger.debug { "Sabotage to ${msg.sabotage} killed everyone" }

                            // Kill everyone!
                            session.players.forEach {
                                if (it.role == PlayerRole.Crewmate) it.player.state = PlayerState.Ghost
                            }

                            // And let the victory check do the rest
                            checkVictory(session)
                        }
                    }
                }
                logger.debug { "Sabotage started" }
            }
            is SabotageFix -> {
                val session = client.session ?: return
                logger.debug { "Sabotage fix request handler called" }
                synchronized(session.sabotageLock) {
                    val sabotage = session.currentSabotage ?: return
                    if (!sabotage.critical && msg.fixing) {
                        logger.debug { "Player ${client.id} fixed $sabotage" }
                        session.clearSabotage()
                    } else if (sabotage == SabotageType.O2) {
                        if (msg.fixing)
                            session.sabotageFixedLocations.add(msg.location)

                        logger.debug { "Player ${client.id} entered ${session.sabotageFixedLocations.size}/2 O2 codes" }

                        if (session.sabotageFixedLocations.size == 2) {
                            session.clearSabotage()
                        }
                    } else if (sabotage == SabotageType.Reactor) {
                        if (msg.fixing)
                            session.sabotageFixingPlayers++
                        else
                            session.sabotageFixingPlayers--

                        logger.debug { "${session.sabotageFixingPlayers}/2 players fixing reactor meltdown" }

                        if (session.sabotageFixingPlayers == 2) {
                            session.clearSabotage()
                        }
                    }
                }
            }
            is CompleteTaskStage -> {
                val session = client.session ?: return
                if (client.role != PlayerRole.Crewmate) return
                session.tasksCompleted++
                session.broadcast(OnTaskProgress(session.tasksCompleted / session.totalTaskStages.toFloat()))
                checkVictory(session)
            }
            is CallMeeting -> {
                val session = client.session ?: return
                logger.info { "Player ${client.id} called an emergency meeting (by_button = ${msg.byButton})" }
                session.broadcast(OnEmergencyMeeting(client.id, msg.byButton))
                session.votes.clear()
                assignSpawnPositions(session)
            }
            is ChangePosition -> {
                client.session?.broadcastExcept(OnPlayerMove(client.id, msg.pos, msg.rot), client.id)
            }
            is ChangeCameraState -> {
                val session = client.session ?: return
                synchronized(session.surveillanceLock) {
                    if (msg.inCams) session.surveillancePlayers++
                    else if (session.surveillancePlayers > 0) session.surveillancePlayers--
                    session.broadcast(OnSurveillanceChanged(session.surveillancePlayers > 0))
                }
            }
            is RequestEjectionResult -> {
                val session = client.session ?: return
                synchronized(session.votingTimerLock) {
                    session.votingTimerCompletePlayers++
                    if (session.votingTimerCompletePlayers >= session.players.size) {
                        val ejectionResult = session.determineEjection()
                        logger.info { "Session ${session.code} voted for $ejectionResult" }
                        session.broadcast(OnEjectionResult(ejectionResult))
                        session.votingTimerCompletePlayers = 0
                        if (ejectionResult.type == EjectResultType.Ejected)
                            session.findPlayer(ejectionResult.player)?.player?.state = PlayerState.Ghost
                        checkVictory(session)
                    }
                }
            }
        }
    }

    private fun assignSpawnPositions(session: ServerSession) {
        val center = Vector3f(28.09f, 0.0f, -22.46f)
        val radius = 2.25f
        val angleIncrement = 2.0f * PI / session.players.size
        var angle = 0f
        session.players.forEach {
            val pos = Vector3f(center).add(MathF.sin(angle) * radius, 0f, MathF.cos(angle) * radius)
            session.broadcast(OnPlayerMove(it.id, pos, 0f))
            angle += angleIncrement
        }
    }

    private fun resetSession(session: ServerSession) {
        session.votes.clear()
        session.tasksCompleted = 0
        session.totalTaskStages = 0
        session.surveillancePlayers = 0
        session.votingTimerCompletePlayers = 0
        session.clearSabotage()
        session.players.forEach {
            it.player.state = PlayerState.Alive
            it.player.role = PlayerRole.Crewmate
        }
    }

    private fun ServerSession.clearSabotage() {
        currentSabotage = null
        sabotageKillTask?.also { scheduler.cancel(it) }
        sabotageKillTask = null
        sabotageFixedLocations.clear()
        sabotageFixingPlayers = 0
        broadcast(OnSabotageChanged(null))
    }

    private fun selectImpostor(session: ServerSession): PlayerClient? =
        session.players.filter { it.role != PlayerRole.Impostor }.shuffled().firstOrNull()

    private fun checkVictory(session: ServerSession) {
        if (disableVictory) return
        val winners = tryFindWinners(session) ?: return
        session.broadcast(OnGameEnded(winners))
        resetSession(session)
    }

    private fun tryFindWinners(session: ServerSession): PlayerRole? {
        if (session.tasksCompleted >= session.totalTaskStages)
            return PlayerRole.Crewmate
        val alivePlayers = session.players.filter { it.state == PlayerState.Alive }
        val numAliveImpostors = alivePlayers.count { it.role == PlayerRole.Impostor }
        val numAliveCrewmates = alivePlayers.count { it.role == PlayerRole.Crewmate }
        return when {
            numAliveImpostors == 0 -> PlayerRole.Crewmate                   // Crewmates ejected all impostors off the ship
            numAliveImpostors >= numAliveCrewmates -> PlayerRole.Impostor   // Not enough crewmates to vote any impostor off -> loss
            else -> null                                                    // The game is still on.
        }
    }

    private fun ServerSession.determineEjection(): EjectResult {
        if (votes.isEmpty())
            return EjectResult(EjectResultType.Tie)

        // Create map of <PlayerId: Int, NumVotes: Int>
        val accumulatedVotes = HashMap<Int, Int>()
        votes.entries.forEach {
            val numVotes = accumulatedVotes[it.value]
            if (numVotes != null)
                accumulatedVotes[it.value] = numVotes + 1
            else
                accumulatedVotes[it.value] = 1
        }

        // Create list of MapEntry<PlayerId, NumVotes>, sorted by NumVotes
        val sortedVotes = accumulatedVotes.entries.sortedBy { it.value }

        // Get the top two entries
        val highestVotes = sortedVotes[0]
        val secondHighestVotes = sortedVotes.getOrNull(1)

        // Determine the ejection result
        return if (secondHighestVotes?.value == highestVotes.value) {
            EjectResult(EjectResultType.Tie)
        } else if (highestVotes.key == IPlayer.INVALID_PLAYER_ID) {
            EjectResult(EjectResultType.Skipped)
        } else {
            EjectResult(EjectResultType.Ejected, highestVotes.key)
        }
    }

}