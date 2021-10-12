package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient

class PausePage : BasePage("Pause.html") {

    fun settings() {
        PageManager.push(SettingsPage())
    }

    fun leaveSession() {
        AmongUsClient.leave()
    }

}