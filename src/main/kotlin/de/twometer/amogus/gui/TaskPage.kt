package de.twometer.amogus.gui

import de.twometer.amogus.client.TaskGameObject
import de.twometer.neko.gui.Page

class TaskPage(taskObject: TaskGameObject) : Page("Tasks/${taskObject.taskType}.html") {

    override fun blocksGameInput(): Boolean {
        return true
    }

}