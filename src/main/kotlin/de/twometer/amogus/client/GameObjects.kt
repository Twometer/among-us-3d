package de.twometer.amogus.client

import de.twometer.amogus.model.Location
import de.twometer.amogus.model.SabotageType
import de.twometer.amogus.model.TaskType
import de.twometer.amogus.model.ToolType
import de.twometer.neko.scene.component.BaseComponent
import de.twometer.neko.scene.nodes.Node

open class GameObject : BaseComponent() {
    override fun createInstance(): BaseComponent =
        throw UnsupportedOperationException("Cannot create new instances of completed game objects, because programmer was lazy.")
}

data class VentGameObject(val location: Location, val number: Int = 1) : GameObject()

data class TaskGameObject(val location: Location, val taskType: TaskType) : GameObject()

data class SabotageGameObject(val location: Location, val sabotageType: SabotageType) : GameObject()

data class ToolGameObject(val location: Location, val toolType: ToolType) : GameObject()

fun createGameObjectFromNodeName(name: String): GameObject? {
    val parts = name.split("_")
    if (parts.size < 2) return null

    return when (parts[0]) {
        "VENT" -> {
            VentGameObject(Location.valueOf(parts[1]), parts[2].toIntOrNull() ?: 1)
        }
        "TASK" -> {
            TaskGameObject(Location.valueOf(parts[1]), TaskType.valueOf(parts[2]))
        }
        "SABOTAGE" -> {
            SabotageGameObject(Location.valueOf(parts[1]), SabotageType.valueOf(parts[2]))
        }
        "TOOL" -> {
            ToolGameObject(Location.valueOf(parts[1]), ToolType.valueOf(parts[2]))
        }
        else -> null
    }
}

fun Node.findGameObject(): GameObject? {
    this.components.forEach {
        val candidate = it.value
        if (candidate is GameObject)
            return candidate
    }
    return parent?.findGameObject()
}