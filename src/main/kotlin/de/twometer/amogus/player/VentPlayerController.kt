package de.twometer.amogus.player

import de.twometer.neko.core.NekoApp
import de.twometer.neko.core.Window
import de.twometer.neko.player.PlayerController
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.MathF

class VentPlayerController : PlayerController {

    var sensitivity = 0.25f

    override fun updateCamera(window: Window, scene: Scene, deltaTime: Double) {
        if (!window.isFocused() || NekoApp.the.cursorVisible)
            return

        val sensitivity = this.sensitivity * deltaTime.toFloat()

        // Rotation
        val (winW, winH) = window.getSize()
        val (curX, curY) = window.getCursorPosition()
        val dx = winW / 2 - curX
        val dy = winH / 2 - curY

        scene.camera.rotation.x += dx.toFloat() * sensitivity
        scene.camera.rotation.y += dy.toFloat() * sensitivity
        scene.camera.rotation.y = MathF.clamp(-MathF.PI / 2f, MathF.PI / 2f, scene.camera.rotation.y)

        window.setCursorPosition(winW / 2, winH / 2)
    }
}