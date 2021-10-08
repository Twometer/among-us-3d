package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.render.CRTFilter
import de.twometer.neko.gui.Page
import de.twometer.neko.util.MathF
import org.joml.Vector2f
import org.joml.Vector3f

data class CamLocation(val x: Float, val y: Float, val z: Float, val xRot: Float, val yRot: Float)

class SurveillancePage : Page("SecurityCamera.html") {

    private val locations = arrayOf(
        CamLocation(10.807461f, 1.25f, -14.47564f, 198.48026f, -62.72f),
        CamLocation(20.94838f, 0.5f, -21.486969f, 246.80066f, -37.999996f),
        CamLocation(31.332027f, 0.375f, -12.794603f, 241.0806f, -32.360012f),
        CamLocation(38.80089f, 0.125f, -11.5932045f, 158.8406f, -17.400011f)
    )
    private var index = 0
    private val savedPosition = Vector3f()
    private val savedRotation = Vector2f()


    override fun onLoaded() {
        AmongUsClient.renderer.effectsPipeline.findStep<CRTFilter>().active = true
        savedPosition.set(AmongUsClient.scene.camera.position)
        savedRotation.set(AmongUsClient.scene.camera.rotation)
        teleport()
    }

    override fun onUnloaded() {
        AmongUsClient.renderer.effectsPipeline.findStep<CRTFilter>().active = false
        AmongUsClient.scene.camera.position.set(savedPosition)
        AmongUsClient.scene.camera.rotation.set(savedRotation)
    }

    override fun blocksGameInput(): Boolean {
        return true
    }

    fun next() {
        index++
        teleport()
    }

    fun prev() {
        index--
        teleport()
    }

    private fun wrapIndex() {
        if (index < 0) index = locations.size - 1
        if (index >= locations.size) index = 0
    }

    private fun teleport() {
        wrapIndex()
        val location = locations[index]
        AmongUsClient.scene.camera.position.set(location.x, location.y, location.z)
        AmongUsClient.scene.camera.rotation.set(MathF.toRadians(location.xRot), MathF.toRadians(location.yRot))
    }

}