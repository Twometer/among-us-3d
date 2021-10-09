package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.neko.audio.SoundEngine

class RoleRevealPage : BasePage(path = "RoleReveal.html") {

    override fun onLoaded() {
        val me = AmongUsClient.session!!.myself
        call("setRole", me.role.name)
        AmongUsClient.session!!.players
            .filter { it.id != me.id }
            .forEach { call("addTeammate", it.username) }
        SoundEngine.play("RoleReveal.ogg")
        AmongUsClient.mainScheduler.runLater(4700L) {
            PageManager.overwrite(IngamePage())
        }
    }

}