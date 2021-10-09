package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient

class LobbyPage : BasePage("Lobby.html") {

    override fun onLoaded() {
        PageManager.clearHistory()
        setElementText("gamecode", AmongUsClient.session!!.code)
        call("setIsHost", AmongUsClient.session!!.myselfIsHost)
    }

    fun customize() {
        PageManager.push(CustomizePage())
    }

    fun start() {

    }

    fun disconnect() {

    }

}