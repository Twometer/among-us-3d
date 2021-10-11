package de.twometer.amogus.model

import de.twometer.neko.scene.nodes.ModelNode
import org.joml.Vector3f

class Player : IPlayer {
    override var id = IPlayer.INVALID_PLAYER_ID
    override var username = ""
    override var role: PlayerRole = PlayerRole.Crewmate
    override var color: PlayerColor = PlayerColor.Red
    val prevPosition: Vector3f = Vector3f(0f, 0f, 0f)
    val position: Vector3f = Vector3f(0f, 0f, 0f)
    var prevRotation = 0f
    var rotation = 0f
    var lastUpdate: Long = 0L
    val speedSquared get() = position.distanceSquared(prevPosition)
    override var state: PlayerState = PlayerState.Alive
    var emergencyMeetings = 0
    var lastMeetingCalled = 0L
    var tasks: List<PlayerTask> = ArrayList()
    var node: ModelNode? = null
    var killCooldown: Int = 0

    fun findTask(location: Location, type: TaskType): PlayerTask? = tasks.firstOrNull {
        val nextStage = it.nextStage
        !it.isCompleted && !it.isTimerRunning && nextStage.location == location && nextStage.taskType == type
    }
}