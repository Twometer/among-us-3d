package de.twometer.amogus.model

data class SessionConfig(
    val impostorCount: Int = 1,
    val playerSpeed: Float = 1.0f,
    val playerVision: Float = 1.0f,
    val impostorVision: Float = 1.25f,
    val killCountdown: Int = 25,
    val emergencyMeetings: Int = 1,
    val confirmEjects: Boolean = true,
    val votingTime: Int = 45,
    val commonTasks: Int = 1,
    val shortTasks: Int = 1,
    val longTasks: Int = 2,
)