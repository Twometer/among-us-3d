package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.model.SabotageType
import de.twometer.amogus.net.SabotageStart

class SabotagePage : BasePage("Sabotage.html") {

    fun sabotage(sabotage: String) {
        if (AmongUsClient.sabotageCooldown != 0)
            return
        AmongUsClient.send(SabotageStart(SabotageType.valueOf(sabotage)))
        close()
    }

}