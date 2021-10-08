package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient

class MainMenuPage : BasePage("MainMenu.html") {

    fun updateUsername(name: String) {

    }

    fun createGame() {

    }

    fun joinGame(code: String) {

    }

    fun settings() {

    }

    fun credits() {

    }

    fun quit() {
        AmongUsClient.window.close()
    }

}