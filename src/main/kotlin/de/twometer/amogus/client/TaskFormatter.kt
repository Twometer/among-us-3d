package de.twometer.amogus.client

import de.twometer.amogus.model.*

object TaskFormatter {

    fun formatTask(task: PlayerTask): String {
        return StringBuilder().apply {
            val stage: TaskStage = task.nextStage
            append(formatLocation(stage.location))
            append(": ")
            append(formatStage(task.progress, stage.taskType, task))

            if (task.isMultiStage) {
                append(" (").append(task.progress).append("/").append(task.length).append(")")
            } else if (task.isTimerRunning) {
                append(" (").append(task.remainingTime).append("s)")
            }
        }.toString()
    }

    fun formatSabotage(sabotage: SabotageType, time: Int): String {
        return I18n["sabotage.$sabotage", time]
    }

    fun formatLocation(location: Location) = I18n["rooms.$location"]

    private fun formatStage(progress: Int, type: TaskType, task: PlayerTask): String {
        return when (type) {
            TaskType.DivertPower -> return I18n["tasks.DivertPower", formatLocation(task.lastStage.location)]
            TaskType.DataTransfer -> if (progress == 0) I18n["tasks.Download"] else I18n["tasks.Upload"]
            else -> I18n["tasks.$type"]
        }
    }

}