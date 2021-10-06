package de.twometer.amogus.client

import de.twometer.amogus.gui.IngamePage
import de.twometer.amogus.player.CollidingPlayerController
import de.twometer.amogus.res.SmlLoader
import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.CubemapCache
import de.twometer.neko.res.ModelCache
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.nodes.Geometry
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.scene.nodes.Sky
import org.greenrobot.eventbus.Subscribe
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

object AmongUsClient : NekoApp(
    AppConfig(
        windowWidth = 1280,
        windowHeight = 720,
        windowTitle = "Among Us 3D"
    )
) {

    private var debugActive = false

    override fun onPreInit() {
        AssetManager.registerPath("./assets")
        loadingPage = "Loading.html"
    }

    override fun onPostInit() {
        // Load skybox
        scene.rootNode.attachChild(Sky(CubemapCache.get("skybox")))

        // Load map model
        scene.rootNode.attachChild(ModelCache.get("skeld.obj").also {
            it.children[0].children.forEach { n ->
                if (!n.name.startsWith("VENT") && !n.name.startsWith("TASK") && !n.name.startsWith("TOOL")) {
                    n.scanTree { c ->
                        if (c is Geometry)
                            c.canPick = false
                    }
                }
            }
        })

        // Load lights
        val lights = SmlLoader.load("lights.sml")
        lights.forEach {
            val position = it.readVec3()
            val color = if (it.hasRemaining) it.readVec3() else Vector3f(1f, 1f, 1f)
            scene.rootNode.attachChild(PointLight().also { light ->
                light.transform.translation.set(position)
                light.color = Color(color.x, color.y, color.z, 1.0f)
            })
        }

        // Other configuration
        pickEngine.maxDistance = 1.8f
        playerController = CollidingPlayerController()

        guiManager.page = IngamePage()
    }

    override fun onRenderFrame() {
        if (debugActive) showDebugWindow()
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW.GLFW_KEY_F3)
            debugActive = !debugActive
    }

}