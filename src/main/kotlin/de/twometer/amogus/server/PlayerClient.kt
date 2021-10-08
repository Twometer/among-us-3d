package de.twometer.amogus.server

import com.esotericsoftware.kryonet.Connection
import de.twometer.amogus.model.IPlayer
import de.twometer.amogus.model.Player
import de.twometer.amogus.model.PlayerColor
import de.twometer.amogus.model.PlayerRole

class PlayerClient : IPlayer, Connection() {

    val player = Player()
    var session: ServerSession? = null

    override val id: Int
        get() = player.id
    override val username: String
        get() = player.username
    override val role: PlayerRole
        get() = player.role
    override val color: PlayerColor
        get() = player.color
    val inSession get() = session != null
    val isHost get() = session?.host == id
    var alive = true
}