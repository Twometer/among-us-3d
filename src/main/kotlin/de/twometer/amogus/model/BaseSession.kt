package de.twometer.amogus.model

abstract class BaseSession<P : IPlayer>(val code: String, var host: Int) {
    val players = ArrayList<IPlayer>()
    var config = SessionConfig()
}