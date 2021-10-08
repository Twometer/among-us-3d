package de.twometer.amogus.server

import de.twometer.amogus.model.BaseSession
import de.twometer.amogus.model.PlayerColor

class ServerSession(code: String, host: Int) : BaseSession<PlayerClient>(code, host) {

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