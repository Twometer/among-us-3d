package de.twometer.amogus.gui

import de.twometer.amogus.client.TaskGameObject
import de.twometer.neko.audio.SoundEngine

class TaskPage(taskObject: TaskGameObject) : BasePage("Tasks/${taskObject.taskType}.html") {

    fun taskComplete() {
        SoundEngine.play("TaskComplete.ogg")
        PageManager.goBack()
    }

}