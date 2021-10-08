package de.twometer.amogus.model

enum class TaskState {
    NotStarted,
    InProgress,
    Completed
}

data class PlayerTask(val stages: MutableList<TaskStage> = ArrayList()) {
    private var progress: Int = 0
    private var timerDst: Long = -1

    val isMultiStage get() = length > 1

    val isCompleted get() = state == TaskState.Completed

    val length get() = stages.size

    val state
        get() = when (progress) {
            0 -> TaskState.NotStarted
            in 1 until stages.size -> TaskState.InProgress
            stages.size -> TaskState.Completed
            else -> throw IllegalStateException("Invalid task progress state")
        }

    val nextStage get() = stages[progress.coerceAtMost(stages.size - 1)]

    val lastStage get() = stages.last()

    val timerRunning get() = timerDst != -1L && !timerEnded

    val timerEnded get() = timerDst != -1L && timerDst <= System.currentTimeMillis()

    val remainingTime get() = if (timerDst == -1L || timerEnded) 0L else ((System.currentTimeMillis() - timerDst) / 1000.0).toLong()

    fun advance() {
        require(!isCompleted) { "Cannot advance a completed task" }
        progress++
    }

    fun startTimer(timeout: Long) {
        timerDst = System.currentTimeMillis() + timeout * 1000
    }

}

data class TaskStage(val location: Location, val taskType: TaskType)