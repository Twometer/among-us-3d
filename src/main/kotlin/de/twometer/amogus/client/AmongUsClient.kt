package de.twometer.amogus.client

import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetManager

object AmongUsClient : NekoApp() {

    override fun onPreInit() {
        AssetManager.registerPath("./assets")
    }

}