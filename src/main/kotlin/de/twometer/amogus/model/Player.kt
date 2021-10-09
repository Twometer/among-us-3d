package de.twometer.amogus.model

import de.twometer.neko.scene.nodes.ModelNode
import org.joml.Vector3f

class Player : IPlayer {
    override var id = IPlayer.INVALID_PLAYER_ID
    override var username = ""
    override var role: PlayerRole = PlayerRole.Crewmate
    override var color: PlayerColor = PlayerColor.Red
    val position: Vector3f = Vector3f(0f, 0f, 0f)
    var rotation = 0f
    var state: PlayerState = PlayerState.Alive
    var emergencyMeetings = 0
    var lastMeetingCalled = 0L
    var tasks: List<PlayerTask> = ArrayList()
    var node: ModelNode? = null
}