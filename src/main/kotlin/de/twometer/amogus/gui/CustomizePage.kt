package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.model.PlayerColor
import de.twometer.amogus.model.SessionConfig
import de.twometer.amogus.net.ColorChangeRequest
import de.twometer.amogus.net.ColorChangeResponse
import de.twometer.amogus.net.SessionConfigureRequest
import de.twometer.amogus.net.SessionConfigureResponse

class CustomizePage : BasePage("Customize.html") {

    private var playerColor: PlayerColor? = null
    private var sessionConfig: SessionConfig? = null

    override fun onLoaded() {
        val values = PlayerColor.values()
        for (i in values.indices) {
            val value = values[i]
            val colorString = "rgb(${value.color.r * 255f}, ${value.color.g * 255f}, ${value.color.b * 255})"
            call("setColorBox", i, colorString, value.name)
            if (value == AmongUsClient.session!!.myself.color)
                call("selectColor", i)
        }
        call("setConfig", AmongUsClient.session!!.config)
        call("setIsHost", AmongUsClient.session!!.myselfIsHost)
        call("initialize")
    }

    fun setConfig(conf: String) {
        sessionConfig = conf.parseJson(SessionConfig::class.java)
    }

    fun setColor(color: String) {
        playerColor = PlayerColor.valueOf(color)
    }

    fun apply() {
        if (playerColor != null && playerColor != AmongUsClient.session!!.myself.color) {
            showLoading("Changing color...")
            AmongUsClient.send(ColorChangeRequest(playerColor!!))
            AmongUsClient.waitFor<ColorChangeResponse> {
                if (!it.accepted) {
                    showError("Color is already taken")
                } else applyConfig()
            }
        } else applyConfig()
    }

    private fun applyConfig() {
        if (!AmongUsClient.session!!.myselfIsHost || sessionConfig == null) {
            close()
            return
        }

        showLoading("Reconfiguring...")
        AmongUsClient.send(SessionConfigureRequest(sessionConfig!!))
        AmongUsClient.waitFor<SessionConfigureResponse> {
            if (!it.accepted)
                showError("Server rejected configuration")
            else
                close()
        }
    }

    fun back() = close()

}