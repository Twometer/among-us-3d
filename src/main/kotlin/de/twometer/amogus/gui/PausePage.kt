package de.twometer.amogus.gui

class PausePage : BasePage("Pause.html") {

    fun settings() {
        PageManager.push(SettingsPage())
    }

    fun leaveSession() {

    }

}