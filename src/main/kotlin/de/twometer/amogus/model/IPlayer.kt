package de.twometer.amogus.model

interface IPlayer {

    companion object {
        const val INVALID_PLAYER_ID: Int = -1
    }

    val id: Int
    val username: String
    val role: PlayerRole
    val color: PlayerColor
}