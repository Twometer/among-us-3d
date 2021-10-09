package de.twometer.amogus.client

import de.twometer.amogus.model.Location
import de.twometer.amogus.model.SabotageType
import de.twometer.amogus.model.TaskType
import de.twometer.amogus.model.ToolType
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.component.BaseComponent
import de.twometer.neko.scene.nodes.Node

abstract class GameObject : BaseComponent() {
    override fun createInstance(): BaseComponent =
        throw UnsupportedOperationException("Cannot create new instances of completed game objects, because programmer was lazy.")

    open fun canInteract(): Boolean {
        return true
    }

    open fun isHighlighted(): Boolean {
        return AmongUsClient.currentPickTarget?.equals(this) == true
    }

    open fun getHighlightColor(): Color {
        return if (AmongUsClient.currentPickTarget?.equals(this) == true)
            Color(1f, 1f, 0f, 1f)
        else
            Color(1f, 1f, 1f, 1f)
    }
}

abstract class LocationBasedInteractableGameObject(protected open val location: Location) : GameObject() {
    override fun canInteract(): Boolean =
        AmongUsClient.currentPlayerLocation == location || location.interactCheckExempt
}

class PlayerGameObject : GameObject() {
    override fun isHighlighted(): Boolean {
        return false // Highlight shader doesn't work for animated objects
    }
}

data class VentGameObject(override val location: Location, val number: Int = 1) :
    LocationBasedInteractableGameObject(location) {
    override fun getHighlightColor(): Color {
        return Color(1f, 0.15f, 0.15f, 1f)
    }
}

data class TaskGameObject(override val location: Location, val taskType: TaskType) :
    LocationBasedInteractableGameObject(location) {
    override fun isHighlighted(): Boolean {
        return AmongUsClient.session?.myself?.tasks?.any { it.nextStage.location == location && it.nextStage.taskType == taskType } == true
    }
}

data class SabotageGameObject(override val location: Location, val sabotageType: SabotageType) :
    LocationBasedInteractableGameObject(location)

data class ToolGameObject(override val location: Location, val toolType: ToolType) :
    LocationBasedInteractableGameObject(location)

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