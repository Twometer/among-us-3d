package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.TaskGameObject
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.gui.Page

class TaskPage(taskObject: TaskGameObject) : Page("Tasks/${taskObject.taskType}.html") {

    override fun blocksGameInput(): Boolean {
        return true
    }

    fun taskComplete() {
        SoundEngine.play("TaskComplete.ogg")
        PageManager.goBack()
    }

}