package de.twometer.amogus.model

abstract class BaseSession<P : IPlayer>(val code: String, var host: Int) {
    val players = ArrayList<P>()
    var config = SessionConfig()

    fun findPlayer(id: Int): P? {
        return players.firstOrNull { it.id == id }
    }

}