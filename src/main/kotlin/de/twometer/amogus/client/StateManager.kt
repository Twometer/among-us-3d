package de.twometer.amogus.client

import de.twometer.amogus.model.GameState
import de.twometer.neko.events.Events
import mu.KotlinLogging

class GameStateChangedEvent(val old: GameState, val new: GameState)

private val logger = KotlinLogging.logger {}

object StateManager {

    var gameState: GameState = GameState.Loading
        private set

    fun changeGameState(newState: GameState) {
        val oldState = gameState
        gameState = newState
        if (oldState != newState) {
            logger.debug { "Game state changed from $oldState to $newState" }
            Events.post(GameStateChangedEvent(oldState, newState))
        }
    }

}