package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.net.OnPlayerLeave
import de.twometer.amogus.net.OnPlayerUpdate
import de.twometer.amogus.net.OnSessionUpdate
import de.twometer.amogus.net.StartGame
import de.twometer.neko.events.Events
import org.greenrobot.eventbus.Subscribe

class LobbyPage : BasePage("Lobby.html") {

    override fun onLoaded() {
        PageManager.clearHistory()
        setElementText("gamecode", AmongUsClient.session!!.code)
        call("setIsHost", AmongUsClient.session!!.myselfIsHost)
        AmongUsClient.session!!.players.forEach {
            call("addPlayer", it.id, it.username, it.color.name)
        }
        Events.register(this)
    }

    override fun onUnloaded() {
        Events.unregister(this)
    }

    @Subscribe
    fun onPlayerLeave(e: OnPlayerLeave) {
        runOnUiThread { call("removePlayer", e.id) }
    }

    @Subscribe
    fun onPlayerUpdate(e: OnPlayerUpdate) {
        runOnUiThread {
            call("removePlayer", e.id)
            call("addPlayer", e.id, e.username, e.color.name)
        }
    }

    @Subscribe
    fun onSessionUpdate(e: OnSessionUpdate) {
        runOnUiThread { call("setIsHost", AmongUsClient.session!!.myselfIsHost) }
    }

    fun customize() {
        PageManager.push(CustomizePage())
    }

    fun start() {
        AmongUsClient.send(StartGame())
    }

    fun disconnect() {
        AmongUsClient.leave()
    }

}