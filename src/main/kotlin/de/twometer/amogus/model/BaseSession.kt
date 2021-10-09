package de.twometer.amogus.model

abstract class BaseSession<P : IPlayer>(var code: String = "", var host: Int = IPlayer.INVALID_PLAYER_ID) {
    val players = ArrayList<P>()
    var config = SessionConfig()

    fun findPlayer(id: Int): P? {
        return players.firstOrNull { it.id == id }
    }

}