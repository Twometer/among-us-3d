package de.twometer.amogus.client

import de.twometer.amogus.gui.IngamePage
import de.twometer.amogus.model.Location
import de.twometer.amogus.player.*
import de.twometer.amogus.render.CRTFilter
import de.twometer.amogus.render.HighlightRenderer
import de.twometer.amogus.render.updateMaterial
import de.twometer.amogus.res.SmlLoader
import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.res.AnimationCache
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.CubemapCache
import de.twometer.neko.res.ModelCache
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.nodes.Geometry
import de.twometer.neko.scene.nodes.ModelNode
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.util.MathExtensions.clone
import de.twometer.neko.util.MathF
import imgui.ImGui
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

    lateinit var collider: LineCollider
    lateinit var boundsTester: BoundsTester
    lateinit var astronautPrefab: ModelNode

    var currentPickTarget: GameObject? = null
        private set
    var currentPlayerLocation: Location = Location.Hallways
        private set

    private var debugActive = false

    override fun onPreInit() {
        AssetManager.registerPath("./assets")
        loadingPage = "Loading.html"
    }

    override fun onPostInit() {
        // Load skybox and bind to unit 16
        val skybox = CubemapCache.get("skybox")
        skybox.bind(16)

        // Load map metadata
        collider = LineColliderLoader.load("collider.sml")
        boundsTester = BoundsTesterLoader.load("bounds.sml")

        // Load map model
        scene.rootNode.attachChild(ModelCache.get("skeld.obj").also {
            it.children[0].children.forEach { node ->
                val gameObject = createGameObjectFromNodeName(node.name)
                if (gameObject != null) {
                    node.attachComponent(gameObject)
                } else {
                    node.scanTree { g -> if (g is Geometry) g.canPick = false }
                }
            }
            it.updateMaterial("Translucent_Glass_Blue.002") { mat ->
                mat.shader = "geometry.space_glass.nks"
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

        // Load astronaut
        astronautPrefab = ModelCache.get("astronaut.fbx").also {
            // Transform for our game
            it.transform.scale.set(0.000035) // so basically, this number is very smol.
            it.transform.rotation.rotateX(MathF.toRadians(90f))

            // Load animations
            it.animations.clear()
            it.animations.add(AnimationCache.get("astronaut_stand.ani"))
            it.animations.add(AnimationCache.get("astronaut_walk.ani"))

            // Update materials
            it.updateMaterial("Suit.001") { mat ->
                mat[MatKey.ColorSpecular] = Color(0.05f, 0.05f, 0.05f, 0.0f)
            }
            it.updateMaterial("Visor.001") { mat ->
                mat[MatKey.ColorSpecular] = Color(1.0f, 1.0f, 1.0f, 0.0f)
            }
        }

        // Add a demo astronaut instance
        scene.rootNode.attachChild(astronautPrefab.createInstance().also {
            it.transform.translation.set(12f, 0f, -15f)
            it.transform.rotation.rotateZ(MathF.toRadians(90f))
            it.playAnimation(it.animations[1])
        })

        // Other configuration
        pickEngine.maxDistance = 1.8f
        playerController = CollidingPlayerController()
        guiManager.page = IngamePage()
        renderer.effectsPipeline.steps.add(CRTFilter().also { it.active = false })
        AmbianceController.play()
    }

    override fun onRenderFrame() {
        if (debugActive) showDebugWindow()

        updatePlayerTests()

        HighlightRenderer.begin()
        scene.rootNode.scanTree { node ->
            val gameObject = node.findGameObject()
            if (node is Geometry && gameObject?.isHighlighted() == true) {
                HighlightRenderer.addNode(node, gameObject.getHighlightColor())
            }
        }
        HighlightRenderer.finish()

        if (currentPickTarget != null) {
            ImGui.begin("Debug Info")
            ImGui.text("Hovering: $currentPickTarget")
            ImGui.end()
        }
    }

    private fun updatePlayerTests() {
        val pickCandidate = pickEngine.pick()?.findGameObject()
        currentPickTarget = if (pickCandidate != null && pickCandidate.canInteract()) pickCandidate else null
        currentPlayerLocation =
            Location.valueOf(boundsTester.findContainingBounds(scene.camera.position.clone()) ?: "Hallways")
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW.GLFW_KEY_F3)
            debugActive = !debugActive
    }

}