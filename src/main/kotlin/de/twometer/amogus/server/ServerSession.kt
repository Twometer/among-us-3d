package de.twometer.amogus.server

import de.twometer.amogus.model.BaseSession
import de.twometer.amogus.model.PlayerColor
import java.util.concurrent.atomic.AtomicInteger

class ServerSession(code: String, host: Int) : BaseSession<PlayerClient>(code, host) {

    val surveillanceLock = Any()
    var surveillancePlayers = 0
    val votingTimerLock = Any()
    var votingTimerCompletePlayers = 0
    var tasksCompleted = 0
    var totalTaskStages = 0
    val votes = HashMap<Int, Int>()
    val isFull: Boolean = players.size >= PlayerColor.values().size

    fun broadcast(packet: Any) {
        players.forEach { it.sendTCP(packet) }
    }

    fun broadcastExcept(packet: Any, except: Int) {
        players.filter { it.id != except }.forEach { it.sendTCP(packet) }
    }

    fun randomColor(): PlayerColor {
        return PlayerColor.values().filter { col -> !players.any { it.color == col } }.shuffled().first()
    }

}