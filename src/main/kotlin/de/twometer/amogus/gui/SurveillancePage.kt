package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.net.ChangeCameraState
import de.twometer.amogus.render.CRTFilter
import de.twometer.neko.gui.Page
import org.joml.Vector2f
import org.joml.Vector3f

data class CamLocation(val x: Float, val y: Float, val z: Float, val xRot: Float, val yRot: Float)

class SurveillancePage : Page("SecurityCamera.html") {

    private val locations = arrayOf(
        CamLocation(1.097E1f, 2.228E0f, -1.437E1f, 3.810E0f, -1.088E0f),
        CamLocation(1.991E1f, 1.018E0f, -2.144E1f, -1.942E0f, -4.327E-1f),
        CamLocation(3.120E1f, 1.122E0f, -1.270E1f, 4.075E0f, -5.008E-1f),
        CamLocation(4.274E1f, 1.016E0f, -1.352E1f, 1.018E1f, -3.201E-1f)
    )
    private var index = 0
    private val savedPosition = Vector3f()
    private val savedRotation = Vector2f()

    override fun onLoaded() {
        AmongUsClient.nosend = true
        AmongUsClient.send(ChangeCameraState(true))
        AmongUsClient.renderer.effectsPipeline.findStep<CRTFilter>().active = true
        savedPosition.set(AmongUsClient.scene.camera.position)
        savedRotation.set(AmongUsClient.scene.camera.rotation)
        teleport()
    }

    override fun onUnloaded() {
        AmongUsClient.renderer.effectsPipeline.findStep<CRTFilter>().active = false
        AmongUsClient.scene.camera.position.set(savedPosition)
        AmongUsClient.scene.camera.rotation.set(savedRotation)
        AmongUsClient.nosend = false
        AmongUsClient.send(ChangeCameraState(false))
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
        AmongUsClient.scene.camera.rotation.set(location.xRot, location.yRot)
    }

}