package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.model.*
import de.twometer.amogus.net.CastVote
import de.twometer.amogus.net.OnEjectionResult
import de.twometer.amogus.net.OnPlayerVoted
import de.twometer.amogus.net.RequestEjectionResult
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.events.Events
import de.twometer.neko.events.TickEvent
import de.twometer.neko.util.Timer
import org.greenrobot.eventbus.Subscribe

class EmergencyPage(val caller: Int) : BasePage("Emergency.html") {

    private val timer = Timer(1)
    private var votingTime = 0
    private var continueTime = 8
    private var pitch = 1.0f
    private var resultsShown = false
    private var votes = HashMap<Int, Int>()

    private fun updateVotingTime() {
        setElementText("votingTimeout", "Voting ends in ${votingTime}s")
    }

    override fun onLoaded() {
        val session = AmongUsClient.session ?: return
        votingTime = session.config.votingTime
        session.players.forEach {
            val isImpostor = it.role == PlayerRole.Impostor && session.myself.role == PlayerRole.Impostor
            val isCaller = caller == it.id
            val isGhost = it.state == PlayerState.Ghost
            call("addPlayer", it.id, it.username, it.color, isGhost, isCaller, isImpostor)
        }
        updateVotingTime()
        Events.register(this)
        timer.reset()
    }

    override fun onUnloaded() {
        Events.unregister(this)
    }

    fun vote(id: Int) {
        val self = AmongUsClient.session?.myselfOrNull ?: return
        if (self.state != PlayerState.Alive) return
        AmongUsClient.send(CastVote(id))
    }

    @Subscribe
    fun onTick(e: TickEvent) {
        if (!timer.elapsed) return
        timer.reset()

        if (resultsShown) {
            continueTime--
            if (continueTime == 0)
                AmongUsClient.send(RequestEjectionResult())
            if (continueTime >= 0)
                setElementText("votingTimeout", "Continuing in ${continueTime}s")
        } else {
            votingTime--
            if (votingTime == 0)
                showResults()
            if (votingTime >= 0)
                updateVotingTime()
            if (votingTime < 4) {
                SoundEngine.play("VoteTimer.ogg").setPitch(pitch)
                pitch += 0.1f
            }
        }
    }

    @Subscribe
    fun onVote(e: OnPlayerVoted) {
        runOnUiThread {
            votes[e.src] = e.dst
            SoundEngine.play("Voted.ogg")
            call("onPlayerVote", e.src)
            if (votes.size >= AmongUsClient.session!!.players.size && votingTime > 1) {
                showResults()
            }
        }
    }

    @Subscribe
    fun onEjectionResult(e: OnEjectionResult) {
        val result = e.result
        runOnUiThread {
            PageManager.overwrite(EjectPage(result))
        }
    }

    private fun showResults() {
        resultsShown = true
        SoundEngine.play("VoteLockin.ogg")
        val idColorMap = HashMap<Int, String>()
        AmongUsClient.session!!.players.forEach { idColorMap[it.id] = it.color.name }
        call("onResults", idColorMap, votes)
    }

}