package de.twometer.amogus.client

import de.twometer.amogus.model.BaseSession
import de.twometer.amogus.model.Player

class ClientSession : BaseSession<Player>() {

    val myself get() = findPlayer(AmongUsClient.myPlayerId)!!
    val myselfOrNull get() = findPlayer(AmongUsClient.myPlayerId)
    val myselfIsHost get() = host == AmongUsClient.myPlayerId
    var taskProgress: Float = 0f

}