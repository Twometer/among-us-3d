package de.twometer.amogus.player

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.model.PlayerState
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.core.NekoApp
import de.twometer.neko.core.Window
import de.twometer.neko.player.PlayerController
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.MathExtensions.clone
import de.twometer.neko.util.MathF
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class CollidingPlayerController : PlayerController {

    var speed = 2.5f
    var sensitivity = 0.25f
    var slipperiness = 0.5f
    var height = 0.75f

    private val prevPos = Vector3f()
    private val velocity = Vector3f()
    private var bobTime = MathF.PI / 2
    private var prevBob = 0f
    private var bobStop = false

    init {
        NekoApp.the.scene.camera.position.set(10f, height, -15f)
    }

    override fun updateCamera(window: Window, scene: Scene, deltaTime: Double) {
        if (!window.isFocused() || NekoApp.the.cursorVisible)
            return

        var speedMul = AmongUsClient.session?.config?.playerSpeed ?: 1.0f
        val isGhost = AmongUsClient.session?.myselfOrNull?.state == PlayerState.Ghost
        if (isGhost)
            speedMul *= 1.75f
        val frameSpeedBase = this.speed * deltaTime.toFloat()
        var speed = frameSpeedBase * speedMul
        val sensitivity = this.sensitivity * deltaTime.toFloat()

        if (window.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            //speed *= 4
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_W)) {
            velocity.add(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_S)) {
            velocity.sub(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_A)) {
            velocity.sub(scene.camera.right.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_D)) {
            velocity.add(scene.camera.right.clone().mul(speed))
        }
        velocity.y = 0f
        if (velocity.lengthSquared() > speed * speed)
            velocity.normalize(speed)

        // Add velocity
        scene.camera.position.add(velocity)
        velocity.mul(slipperiness)

        // Physics
        if (!AmongUsClient.noclip)
            AmongUsClient.collider.processPosition(scene.camera.position)

        // View bobbing
        val actualSpeed = prevPos.sub(scene.camera.position).also { it.y = 0f }.length()
        prevPos.set(scene.camera.position)

        val speedFactor = MathF.max(actualSpeed / frameSpeedBase, 0.0f)
        val baseBob = 0.04f
        val factor = baseBob * speedFactor
        if (actualSpeed > 0.01) {
            bobTime += (factor * deltaTime).toFloat()
            val bobSpeed = if (isGhost) 150 else 480
            val bobStrength = if (isGhost) 0.86f else 0.4f
            val viewBob = MathF.sin(bobTime * bobSpeed) * MathF.min(factor, baseBob) * bobStrength
            scene.camera.position.y = height + viewBob

            if (prevBob < 0 && viewBob < 0 && viewBob > prevBob && !bobStop && !isGhost) {
                bobStop = true
                playFootstep()
            }
            if (prevBob > 0 && viewBob > 0)
                bobStop = false
            prevBob = viewBob
        } else {
            bobTime = MathF.PI / 2
        }

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

    private fun playFootstep() {
        val location = AmongUsClient.currentPlayerLocation
        val rand = (MathF.rand() * location.footstepSound.number).toInt() + 1
        SoundEngine.play("Footsteps/${location.footstepSound}$rand.ogg")
    }

}