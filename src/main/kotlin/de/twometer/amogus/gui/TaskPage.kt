package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.TaskFormatter
import de.twometer.amogus.client.TaskGameObject
import de.twometer.amogus.model.Location
import de.twometer.amogus.model.PlayerState
import de.twometer.amogus.model.PlayerTask
import de.twometer.amogus.model.TaskType
import de.twometer.amogus.net.CompleteTaskStage
import de.twometer.neko.audio.SoundEngine
import kotlin.math.floor
import kotlin.random.Random

class TaskPage(private val taskObject: TaskGameObject, private val task: PlayerTask) : BasePage("Tasks/${taskObject.taskType}.html") {

    override fun onLoaded() {
        val type = taskObject.taskType
        val location = taskObject.location
        if (type == TaskType.DataTransfer) {
            call("ConfigureTaskUI", location == Location.Admin, TaskFormatter.formatLocation(location))
        } else if (type == TaskType.DivertPower) {
            val destination = task.lastStage.location.name.lowercase()
            call("setSliderActive", destination)
        } else if (type == TaskType.Scan) {
            val player = AmongUsClient.session!!.myself
            val hash = player.username.hashCode()
            val random = Random(hash)
            val id = Integer.toHexString(hash).uppercase()
            val height = random.nextInt(15) + 55
            val weight = random.nextInt(20) + 35
            val blood = arrayOf("A", "B", "AB", "0")[random.nextInt(4)]
            val state = if (player.state == PlayerState.Ghost) "GHOST" else "ALIVE"
            call("setProperties", id, player.color.name, height, weight, blood, state)
        } else if (type == TaskType.InspectSamples && task.isTimerEnded) {
            call("setAnomaly", floor(Math.random() * 5))
        }
    }

    fun beginTimeout() {
        task.startTimer(55)
    }

    fun taskComplete() {
        task.advance()
        if (task.isCompleted)
            SoundEngine.play("TaskComplete.ogg")
        else
            SoundEngine.play("TaskProgress.ogg")
        AmongUsClient.send(CompleteTaskStage())
        AmongUsClient.mainScheduler.runLater(800) {
            close()
        }
    }

}