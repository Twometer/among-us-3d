package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.SabotageGameObject
import de.twometer.amogus.model.SabotageType
import de.twometer.amogus.net.SabotageFix
import de.twometer.neko.events.Events
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class FixSabotagePage(private val sabotage: SabotageGameObject) : BasePage("Tasks/Fix${sabotage.sabotageType}.html") {

    override fun onLoaded() {
        if (AmongUsClient.currentSabotage == SabotageType.O2)
            call("setCode", AmongUsClient.currentSabotageCode)
        Events.register(this)
    }

    override fun onUnloaded() {
        Events.unregister(this)
    }

    fun setFixing(fix: Boolean) {
        AmongUsClient.send(SabotageFix(sabotage.location, fix))
        logger.debug { "Fix state $fix at ${sabotage.location}" }
    }

}