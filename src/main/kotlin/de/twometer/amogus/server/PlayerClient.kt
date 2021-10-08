package de.twometer.amogus.server

import com.esotericsoftware.kryonet.Connection
import de.twometer.amogus.model.IPlayer
import de.twometer.amogus.model.Player
import de.twometer.amogus.model.PlayerColor
import de.twometer.amogus.model.PlayerRole

class PlayerClient : IPlayer, Connection() {

    val player = Player()

    override val id: Int
        get() = player.id
    override val username: String
        get() = player.username
    override val role: PlayerRole
        get() = player.role
    override val color: PlayerColor
        get() = player.color


}