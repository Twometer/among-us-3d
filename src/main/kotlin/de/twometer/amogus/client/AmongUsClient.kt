package de.twometer.amogus.client

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import de.twometer.amogus.concurrency.Scheduler
import de.twometer.amogus.gui.*
import de.twometer.amogus.model.*
import de.twometer.amogus.net.*
import de.twometer.amogus.player.*
import de.twometer.amogus.render.CRTFilter
import de.twometer.amogus.render.HighlightRenderer
import de.twometer.amogus.render.updateMaterial
import de.twometer.amogus.res.SmlLoader
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.events.MouseClickEvent
import de.twometer.neko.player.DefaultPlayerController
import de.twometer.neko.player.PlayerController
import de.twometer.neko.render.pipeline.*
import de.twometer.neko.res.*
import de.twometer.neko.scene.AABB
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.component.BoundingBoxProviderComponent
import de.twometer.neko.scene.nodes.*
import de.twometer.neko.util.MathExtensions.clone
import de.twometer.neko.util.MathF
import de.twometer.neko.util.Profiler
import imgui.ImGui
import imgui.type.ImString
import mu.KotlinLogging
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD
import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL
import org.lwjgl.openal.AL10.AL_GAIN
import org.lwjgl.openal.AL10.alListenerf
import java.util.function.Consumer
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

object AmongUsClient : NekoApp(
    AppConfig(
        windowWidth = 1280,
        windowHeight = 720,
        windowTitle = "Among Us 3D",
        windowIcon = "icon.png",
        timerSpeed = 20
    )
) {

    lateinit var collider: LineCollider
    lateinit var boundsTester: BoundsTester
    lateinit var astronautPrefab: ModelNode
    lateinit var netClient: Client

    var currentPickTarget: GameObject? = null
        private set
    var currentPlayerLocation: Location = Location.Hallways
        private set
    var nosend = false
    var noclip = false
    var session: ClientSession? = null
    var myPlayerId = IPlayer.INVALID_PLAYER_ID
    var netY = 0.0f

    private var debugActive = false
    private var consoleActive = false
    private val currentCommand = ImString()
    val packetConsumers = ArrayList<PacketConsumer<*>>()
    val mainScheduler = Scheduler()
    var gameConfig = GameConfig()
    private var prevCtrl: PlayerController? = null

    class PacketConsumer<T>(val packetClass: Class<T>, val consumer: Consumer<T>)

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

            // Apply custom bounding box
            it.scanTree(ScanFilters.GEOMETRY) { geo ->
                geo.attachComponent(BoundingBoxProviderComponent {
                    AABB(Vector3f(-0.5f, 0f, -0.5f), Vector3f(0.5f, 0.85f, 0.5f))
                })
            }
        }

        // Game config
        gameConfig = WorkingDirectory.load(GameConfig.FILE_NAME, GameConfig::class.java, gameConfig)!!

        // Other configuration
        pickEngine.maxDistance = 1.8f
        playerController = CollidingPlayerController()
        guiManager.registerGlobalObject("_api", GuiApi())
        guiManager.page = MainMenuPage()
        renderer.effectsPipeline.steps.add(CRTFilter().also { it.active = false })

        // Networking
        netClient = Client()
        registerAllNetMessages(netClient.kryo)
        netClient.start()
        netClient.connect(5000, "10.0.2.140", 32783)
        netClient.addListener(object : Listener() {
            override fun received(p0: Connection?, p1: Any?) {
                handlePacket(p1!!)
            }
        })

        send(HandshakeRequest(2))
        waitFor<HandshakeResponse> {
            if (!it.accepted) {
                logger.error { "Invalid protocol" }
                exitProcess(0)
            }
            logger.info { "Player id assigned: ${it.playerId}" }
            myPlayerId = it.playerId
        }

        StateManager.changeGameState(GameState.Menus)
    }

    fun applyConfig() {
        alListenerf(AL_GAIN, gameConfig.volume / 100.0f)
        renderer.effectsPipeline.findStep<AmbientOcclusion>().active = gameConfig.aoActive
        renderer.effectsPipeline.findStep<Bloom>().active = gameConfig.bloomActive
        renderer.effectsPipeline.findStep<SSR>().active = gameConfig.ssrActive
        renderer.effectsPipeline.findStep<FXAA>().active = gameConfig.fxaaActive
        renderer.effectsPipeline.findStep<Vignette>().active = gameConfig.vignetteActive
        renderer.lightRadius = gameConfig.lightRenderDist
        fpsLimit = gameConfig.maxFps
    }

    fun saveConfig() {
        WorkingDirectory.store(GameConfig.FILE_NAME, gameConfig)
    }

    fun createAstronautInstance(position: Vector3f, rotation: Float, name: String, color: PlayerColor): ModelNode {
        val instance = astronautPrefab.createInstance().also {
            it.transform.translation.set(position)
            it.transform.rotation.rotateZ(rotation)
            it.playAnimation(it.animations[1])
            it.attachChild(Billboard(FontCache.get("lucida"), name).also { nametag ->
                nametag.transform.scale.set(5000f, 5000f, 5000f)
                nametag.transform.translation.set(0f, 1.25f, 0f)
            })
            it.updateMaterial("Suit.001") { mat ->
                mat[MatKey.ColorDiffuse] = color.color
            }
            it.attachComponent(PlayerGameObject())
        }
        scene.rootNode.attachChild(instance)
        return instance
    }

    fun ModelNode.updateAstronautInstance(name: String, color: PlayerColor) {
        val nametag = this.children.first { it is Billboard } as Billboard
        nametag.text = name
        updateMaterial("Suit.001") {
            it[MatKey.ColorDiffuse] = color.color
        }
    }

    private fun handlePacket(packet: Any) {
        EventBus.getDefault().post(packet)
        val acceptedConsumers = ArrayList<PacketConsumer<Any>>()
        synchronized(packetConsumers) {
            val iter = packetConsumers.iterator()
            while (iter.hasNext()) {
                val consumer = iter.next()
                if (consumer.packetClass == packet.javaClass) {
                    acceptedConsumers.add(consumer as PacketConsumer<Any>)
                    iter.remove()
                }
            }
        }
        acceptedConsumers.forEach { mainScheduler.runNow { it.consumer.accept(packet) } }
    }

    fun send(obj: Any) = netClient.sendTCP(obj)

    inline fun <reified T> waitFor(consumer: Consumer<T>) {
        synchronized(packetConsumers) {
            packetConsumers.add(PacketConsumer(T::class.java, consumer))
        }
    }

    override fun onRenderFrame() {
        if (debugActive) showDebugWindow()

        updatePlayerTests()

        HighlightRenderer.begin()
        scene.rootNode.scanTree(ScanFilters.GEOMETRY) {
            val node = it as Geometry
            val gameObject = node.findGameObject()
            if (gameObject?.isHighlighted() == true) {
                HighlightRenderer.addNode(node, gameObject.getHighlightColor())
            }
        }
        HighlightRenderer.finish()

        if (currentPickTarget != null) {
            ImGui.begin("Debug Info")
            ImGui.text("Hovering: $currentPickTarget")
            ImGui.end()
        }
        if (consoleActive) {
            ImGui.begin("Debug console")
            ImGui.inputText("Command", currentCommand)
            ImGui.setKeyboardFocusHere()
            ImGui.end()
        }

        mainScheduler.update()

        session?.players?.forEach {
            val progress = MathF.clamp(0.0f, 1.0f, (System.currentTimeMillis() - it.lastUpdate) / 50.0f)
            val position = MathF.lerp(it.prevPosition, it.position, progress)
            val rotation = MathF.lerp(it.prevRotation, it.rotation, progress)
            it.node?.apply {
                transform.translation.set(position)
                transform.rotation.identity().rotateX(1.5708f).rotateZ(-rotation)
                if (it.speedSquared < 0.001)
                    playAnimation(animations[0])
                else
                    playAnimation(animations[1])
            }
        }
    }

    private fun updatePlayerTests() {
        val pickCandidate = if (guiManager.page is VentPage) null else pickEngine.pick()?.findGameObject()
        currentPickTarget = if (pickCandidate != null && pickCandidate.canInteract()) pickCandidate else null
        currentPlayerLocation =
            Location.valueOf(boundsTester.findContainingBounds(scene.camera.position.clone()) ?: "Hallways")
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        when {
            e.key == GLFW.GLFW_KEY_F3 -> debugActive = !debugActive
            e.key == GLFW.GLFW_KEY_ESCAPE -> PageManager.goBack()
            e.key == GLFW_KEY_PERIOD && window.isKeyDown(GLFW_KEY_RIGHT_CONTROL) -> {
                consoleActive = !consoleActive
                cursorVisible = consoleActive
            }
            e.key == GLFW.GLFW_KEY_ENTER && consoleActive -> {
                consoleActive = false
                cursorVisible = false
                runCommand(currentCommand.get())
                currentCommand.set("")
            }
        }
    }

    @Subscribe
    fun onMouseClick(e: MouseClickEvent) {
        if (guiManager.isInputBlocked() || cursorVisible) return
        when (val clicked = currentPickTarget) {
            null -> return
            is VentGameObject -> {
                PageManager.push(VentPage(clicked))
            }
            is TaskGameObject -> {
                val task = session!!.myself.findTask(clicked.location, clicked.taskType)
                if (task != null)
                    PageManager.push(TaskPage(clicked, task))
            }
            is ToolGameObject -> {
                if (clicked.toolType == ToolType.Surveillance) PageManager.push(SurveillancePage())
                else if (clicked.toolType == ToolType.Emergency) PageManager.push(CallMeetingPage())
            }
            is SabotageGameObject -> {
                PageManager.push(FixSabotagePage(clicked))
            }
        }
    }

    private fun runCommand(command: String) {
        when (command) {
            "profiler" -> Profiler.enabled = !Profiler.enabled
            "getpos" -> println("Position=${scene.camera.position} Rotation=${scene.camera.rotation}")
            "respawn" -> scene.camera.position.set(10f, 0.75f, -15f)
            "crash" -> mainScheduler.runNow { throw RuntimeException("simulated crash") }
            "noclip" -> noclip = !noclip
            "ingame" -> PageManager.overwrite(IngamePage())
            "impostor" -> session?.myself?.apply { role = PlayerRole.Impostor }
            "freecam" -> {
                if (prevCtrl != null) {
                    playerController = prevCtrl!!
                    prevCtrl = null
                } else {
                    prevCtrl = playerController
                    playerController = DefaultPlayerController()
                }
            }
        }
    }

    override fun onTimerTick() {
        if (StateManager.gameState == GameState.Ingame && !nosend) {
            val pos = scene.camera.position.clone()
            pos.y = netY
            send(ChangePosition(pos, scene.camera.rotation.x))
        }
    }

    @Subscribe
    fun onGameStateChange(e: GameStateChangedEvent) {
        if (e.new == GameState.Ingame)
            AmbianceController.play()
        else
            AmbianceController.stop()
    }

    /// Packet handling ///
    @Subscribe
    fun onSessionJoined(e: SessionJoinResponse) {
        if (e.accepted) {
            session = ClientSession()
            logger.debug { "Initialized new empty session, reason: SessionJoinResponse.accepted == true" }
        } else {
            session = null
            logger.debug { "Failed to join session, reason: ${e.reason}" }
        }
    }

    @Subscribe
    fun onSessionUpdate(e: OnSessionUpdate) {
        logger.debug { "Update session: code=${e.code}; host=${e.host}; config=${e.config}" }
        session!!.code = e.code
        session!!.host = e.host
        session!!.config = e.config
    }

    @Subscribe
    fun onPlayerJoin(e: OnPlayerJoin) {
        logger.debug { "Added new empty player, id=${e.id}" }
        session!!.players.add(Player().also {
            it.id = e.id
        })
    }

    @Subscribe
    fun onPlayerUpdate(e: OnPlayerUpdate) {
        logger.debug { "Updated player ${e.id} (self?=${e.id == myPlayerId}) to color=${e.color}; role=${e.role}; user=${e.username}" }
        session?.findPlayer(e.id)?.apply {
            color = e.color
            role = e.role
            username = e.username
        }
    }

    @Subscribe
    fun onPlayerLeave(e: OnPlayerLeave) {
        logger.debug { "Player ${e.id} leaving current session" }
        session!!.players.removeIf { it.id == e.id }
    }

    @Subscribe
    fun onPlayerMove(e: OnPlayerMove) {
        session?.findPlayer(e.id)?.apply {
            prevPosition.set(position)
            position.set(e.pos)
            prevRotation = rotation
            rotation = e.rot
            lastUpdate = System.currentTimeMillis()
        }
        if (e.id == myPlayerId) {
            scene.camera.position.x = e.pos.x
            scene.camera.position.z = e.pos.z
            scene.camera.rotation.x = e.rot
        }
    }

    @Subscribe
    fun onGameStarted(e: OnGameStarted) {
        logger.debug { "Received game start command from server. Server assigned tasks: ${e.tasks}" }
        val self = session!!.myself
        self.tasks = e.tasks
        self.emergencyMeetings = session!!.config.emergencyMeetings
        self.lastMeetingCalled = System.currentTimeMillis()
        self.state = PlayerState.Alive
        session!!.taskProgress = 0f
        mainScheduler.runNow {
            PageManager.overwrite(RoleRevealPage())

            logger.debug { "Creating player model instances for current session" }
            scene.rootNode.detachAll { it is ModelNode && it.components.containsKey(PlayerGameObject::class.java) }
            session!!.players
                .filter { it.id != myPlayerId }
                .forEach {
                    it.node = createAstronautInstance(it.position, it.rotation, it.username, it.color)
                }
            createAstronautInstance(Vector3f(12f, 0f, -15f), 1.45f, "Test Subject", PlayerColor.LightBlue)
        }
        StateManager.changeGameState(GameState.Ingame)
    }

    @Subscribe
    fun onKill(e: OnPlayerKilled) {
        session?.findPlayer(e.id)?.apply { state = PlayerState.Ghost }
    }

    @Subscribe
    fun onTaskProgressChanged(e: OnTaskProgress) {
        session!!.taskProgress = e.progress
    }

    @Subscribe
    fun onEmergencyMeeting(e: OnEmergencyMeeting) {
        SoundEngine.play(if (e.byButton) "EmergencyMeeting.ogg" else "EmergencyBody.ogg")
    }

    @Subscribe
    fun onSurveillanceChanged(e: OnSurveillanceChanged) {
        session!!.surveillanceActive = e.surveillance
    }

}