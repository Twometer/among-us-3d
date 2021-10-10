package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.TaskFormatter
import de.twometer.amogus.client.VentGameObject
import de.twometer.amogus.model.VentOpening
import de.twometer.amogus.model.Vents
import de.twometer.amogus.player.VentPlayerController
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.events.Events
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.player.PlayerController
import de.twometer.neko.util.MathExtensions.clone
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.glfw.GLFW
import kotlin.math.floor

class VentPage(vent: VentGameObject) : BasePage("Vent.html") {

    private var prevController: PlayerController? = null
    private var current = VentOpening(vent.location, vent.number)
    private val openings = Vents.findVentOpenings(vent.location, vent.number)
    private var index = 0

    override fun onLoaded() {
        AmongUsClient.netY = -3f
        prevController = AmongUsClient.playerController
        AmongUsClient.playerController = VentPlayerController()
        index = openings.indices.first { openings[it] == current }
        tpToVent()
        Events.register(this)
        SoundEngine.play("Vent.ogg")
    }

    override fun onUnloaded() {
        AmongUsClient.netY = 0.0f
        AmongUsClient.playerController = prevController!!
        Events.unregister(this)
        SoundEngine.play("Vent.ogg")
    }

    override fun blocksGameInput(): Boolean {
        return false
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW.GLFW_KEY_SPACE)
            crawlNext()
    }

    private fun crawlNext() {
        index++
        index %= openings.size
        current = openings[index]
        tpToVent()

        SoundEngine.play("VentMove${1 + floor(Math.random() * 3).toInt()}.ogg")
    }

    private fun tpToVent() {
        val ventPos = Vents.findVentPosition(current).clone()
        ventPos.y = 0.15f
        AmongUsClient.scene.camera.position.set(ventPos)

        setElementText(
            "nextlocation",
            TaskFormatter.formatLocation(openings[(index + 1) % openings.size].location)
        )
        setElementText(
            "curlocation",
            TaskFormatter.formatLocation(openings[index].location)
        )
    }

}