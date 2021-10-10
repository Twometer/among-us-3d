package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.GameConfig

class SettingsPage : BasePage("Settings.html") {

    override fun onLoaded() {
        call("set", AmongUsClient.gameConfig)
    }

    fun apply(settingsJson: String) {
        val settings = settingsJson.parseJson(GameConfig::class.java)
        AmongUsClient.gameConfig.set(settings)
        AmongUsClient.saveConfig()
        AmongUsClient.applyConfig()
        close()
    }

}