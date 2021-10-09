package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.net.StartGame

class LobbyPage : BasePage("Lobby.html") {

    override fun onLoaded() {
        PageManager.clearHistory()
        setElementText("gamecode", AmongUsClient.session!!.code)
        call("setIsHost", AmongUsClient.session!!.myselfIsHost)
        AmongUsClient.session!!.players.forEach {
            call("addPlayer", it.id, it.username, it.color.name)
        }
    }

    fun customize() {
        PageManager.push(CustomizePage())
    }

    fun start() {
        AmongUsClient.send(StartGame())
    }

    fun disconnect() {

    }

}