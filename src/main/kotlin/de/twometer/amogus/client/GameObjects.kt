package de.twometer.amogus.client

import de.twometer.amogus.model.Location
import de.twometer.amogus.model.SabotageType
import de.twometer.amogus.model.TaskType
import de.twometer.amogus.model.ToolType
import de.twometer.neko.scene.component.BaseComponent
import de.twometer.neko.scene.nodes.Node

abstract class GameObject : BaseComponent() {
    override fun createInstance(): BaseComponent =
        throw UnsupportedOperationException("Cannot create new instances of completed game objects, because programmer was lazy.")

    open fun canInteract(): Boolean {
        return true
    }
}

abstract class LocationBasedInteractableGameObject(protected open val location: Location) : GameObject() {
    override fun canInteract(): Boolean = AmongUsClient.currentPlayerLocation == location
}

data class VentGameObject(override val location: Location, val number: Int = 1) : LocationBasedInteractableGameObject(location)

data class TaskGameObject(override val location: Location, val taskType: TaskType) : LocationBasedInteractableGameObject(location)

data class SabotageGameObject(override val location: Location, val sabotageType: SabotageType) : LocationBasedInteractableGameObject(location)

data class ToolGameObject(override val location: Location, val toolType: ToolType) : LocationBasedInteractableGameObject(location)

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