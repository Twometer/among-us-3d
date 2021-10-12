package de.twometer.amogus.model

import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseSession<P : IPlayer>(var code: String = "", var host: Int = IPlayer.INVALID_PLAYER_ID) {
    val players = CopyOnWriteArrayList<P>()
    var config = SessionConfig()

    fun findPlayer(id: Int): P? {
        return players.firstOrNull { it.id == id }
    }

}