package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.net.SessionCreateRequest
import de.twometer.amogus.net.SessionCreateResponse
import de.twometer.amogus.net.SessionJoinRequest
import de.twometer.amogus.net.SessionJoinResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class MainMenuPage : BasePage("MainMenu.html") {

    fun updateUsername(name: String) {
        AmongUsClient.gameConfig.username = name
        AmongUsClient.saveConfig()
    }

    override fun onLoaded() {
        setElementProperty("username", "value", AmongUsClient.gameConfig.username)
    }

    fun createGame() {
        showLoading("Creating session...")
        AmongUsClient.send(SessionCreateRequest())
        AmongUsClient.waitFor<SessionCreateResponse> {
            if (!it.accepted)
                showError("Server rejected session creation")
            else
                joinGame(it.code)
        }
    }

    fun joinGame(code: String) {
        logger.info { "Attempting to join game '$code'" }
        if (code.isBlank()) {
            showError("Please enter a game code!")
            return
        }

        showLoading("Joining...")
        AmongUsClient.send(SessionJoinRequest(code, AmongUsClient.gameConfig.username))
        AmongUsClient.waitFor<SessionJoinResponse> {
            if (!it.accepted)
                showError(it.reason)
            else
                PageManager.push(LobbyPage())
        }
    }

    fun settings() {
        PageManager.push(SettingsPage())
    }

    fun credits() {

    }

    fun quit() {
        AmongUsClient.window.close()
    }

}