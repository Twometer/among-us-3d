package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient

class DeathPage : BasePage("Death.html") {

    override fun onLoaded() {
        AmongUsClient.mainScheduler.runLater(4500) {
            PageManager.overwrite(IngamePage())
        }
    }

}