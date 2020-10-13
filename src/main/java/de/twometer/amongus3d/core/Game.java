package de.twometer.amongus3d.core;

import de.twometer.amongus3d.audio.*;
import de.twometer.amongus3d.client.AmongUsClient;
import de.twometer.amongus3d.io.ColliderLoader;
import de.twometer.amongus3d.io.MapLoader;
import de.twometer.amongus3d.io.ModelLoader;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.mesh.shading.ShadingStrategies;
import de.twometer.amongus3d.mesh.shading.ShadingStrategy;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.PlayerTask;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.model.player.Sabotage;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskType;
import de.twometer.amongus3d.obj.GameObject;
import de.twometer.amongus3d.obj.TaskGameObject;
import de.twometer.amongus3d.phys.Collider;
import de.twometer.amongus3d.postproc.PostProcessing;
import de.twometer.amongus3d.postproc.SSAO;
import de.twometer.amongus3d.render.*;
import de.twometer.amongus3d.render.shaders.*;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.ui.screen.MainMenuScreen;
import de.twometer.amongus3d.util.Debug;
import de.twometer.amongus3d.util.Fps;
import de.twometer.amongus3d.util.Log;
import de.twometer.amongus3d.util.Timer;
import org.greenrobot.eventbus.EventBus;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Game {

    private static final Game gameInstance = new Game();

    private static final boolean FAST_ENTRY = false;
    private static final boolean DEBUG_MODE = false;

    public static final String NAME = "Among Us 3D";
    public static final String VERSION = "beta-0.1";

    private static final float VISION_CREWMATE = 5.0f;
    private static final float VISION_IMPOSTOR = 9.0f;
    private static final float VISION_GHOST = 100.0f;
    private static final float VISION_SABOTAGED = 0.5f;

    private final GameWindow window = new GameWindow(NAME + " " + VERSION + (DEBUG_MODE ? " [Debug]" : ""), 1024, 768);
    private final Timer updateTimer = new Timer(90);
    private final Timer networkTimer = new Timer(25);
    private final ShaderProvider shaderProvider = new ShaderProvider();
    private final TextureProvider textureProvider = new TextureProvider();
    private final SoundProvider soundProvider = new SoundProvider();
    private final Camera camera = new Camera();
    private final Fps fps = new Fps();
    private final Debug debug = new Debug();
    private final FrustumCulling frustumCulling = new FrustumCulling();

    private Collider shipCollider;
    private final List<GameObject> gameObjects = new ArrayList<>();
    private Matrix4f viewMatrix;
    private Matrix4f projMatrix;
    private Matrix4f guiMatrix;

    private final PostProcessing postProcessing = new PostProcessing();
    private int ssaoNoiseTexture;
    private ShaderSSAO ssaoShader;
    private ShaderVGauss vGaussShader;
    private ShaderHGauss hGaussShader;
    private ShaderVAvgBlur vAvgBlurShader;
    private ShaderHAvgBlur hAvgBlurShader;
    private ShaderMul mulShader;
    private ShaderCopy copyShader;
    private Framebuffer sceneBuffer;
    private Framebuffer vGaussBuffer;
    private Framebuffer hGaussBuffer;
    private Framebuffer ssaoBuffer;
    private Framebuffer pickBuffer;
    private Framebuffer highlightBuffer;
    private Framebuffer bloomBuffer;

    private Model playerModel;

    private final GuiRenderer guiRenderer = new GuiRenderer();
    private final ByteBuffer pickedBytes = BufferUtils.createByteBuffer(3);

    private ShadingStrategy shadingStrategy = ShadingStrategies.DEFAULT;

    private final GameState gameState = new GameState();

    private final AmongUsClient client = new AmongUsClient();

    private Player self;

    private Game() {
    }

    public static Game instance() {
        return gameInstance;
    }

    public void run() {
        window.create();
        setup();

        while (!window.shouldClose()) {
            renderFrame();
            window.update();
        }

        window.destroy();
    }

    private void setup() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0, 0, 0, 0);

        //// INIT GLFW ////
        window.setClickCallback(button -> {
            if (gameState.getCurrentState() == GameState.State.Running) {
                for (GameObject object : gameObjects)
                    if (object.isSelected())
                        object.onClicked();
            }

            guiRenderer.onClick((int) window.getCursorPosition().x, (int) window.getCursorPosition().y);
        });
        window.setCharTypedCallback(guiRenderer::onCharTyped);
        prevWidth = window.getWidth();
        prevHeight = window.getHeight();
        handleSizeChange(window.getWidth(), window.getHeight());
        ssaoNoiseTexture = SSAO.createNoiseTexture(11, 11);

        //// PRE-INIT ////
        EventBus.builder().logNoSubscriberMessages(false).installDefaultEventBus();
        gameState.setCurrentState(GameState.State.Loading);
        self = new Player("Debug");

        //// LOADING SCREEN ////
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        guiRenderer.init();
        guiRenderer.render();
        window.update();

        //// INIT OBJECTS ////
        playerModel = ModelLoader.load("models/astronaut.obj");
        gameObjects.addAll(MapLoader.loadMap("models/the_skeld.obj"));
        shipCollider = ColliderLoader.loadCollider("models/collider.obj");
        shipCollider.prepareDebugRender();

        Log.i("Loaded " + gameObjects.size() + " game objects.");

        for (GameObject object : gameObjects)
            object.init();

        ssaoShader = shaderProvider.getShader(ShaderSSAO.class);
        vGaussShader = shaderProvider.getShader(ShaderVGauss.class);
        hGaussShader = shaderProvider.getShader(ShaderHGauss.class);
        vAvgBlurShader = shaderProvider.getShader(ShaderVAvgBlur.class);
        hAvgBlurShader = shaderProvider.getShader(ShaderHAvgBlur.class);
        mulShader = shaderProvider.getShader(ShaderMul.class);
        copyShader = shaderProvider.getShader(ShaderCopy.class);
        postProcessing.initialize();

        debug.init();
        debug.addDebugPos(new Vector3f());
        debug.setActive(DEBUG_MODE);

        //// INIT SOUND ////
        Log.i("Starting sound system");
        OpenAL.create();
        gameState.init();

        addAmbience("reactor", 4.16f, 1.37f, -15.38f);
        addAmbience("engine", 9.98f, 1.26f, -22.48f).setGain(1f).setRolloffFactor(5);
        addAmbience("engine", 9.98f, 1.26f, -8.33f).setGain(1f).setRolloffFactor(5);
        addAmbience("security", 14.39f, 0.7f, -18.53f).setGain(0.5f).setRolloffFactor(2);
        addAmbience("medbay", 18.92f, 2.25f, -19.11f).setGain(1.2f).setRolloffFactor(3.5f);
        addAmbience("cafeteria", 32.35f, 0.4f, -27.46f).setGain(1.4f);
        addAmbience("weapons", 39.28f, 0.86f, -22.86f).setGain(0.75f).setRolloffFactor(4);
        addAmbience("oxygen", 36.7f, 0.45f, -18.35f).setRolloffFactor(1.4f);
        addAmbience("hallways", 49.42f, 0.86f, -16).setGain(1.2f).setRolloffFactor(1.2f);
        addAmbience("shields", 41.5f, 0.7f, -8.14f).setRolloffFactor(1.4f);
        addAmbience("shields", 37.72f, 0.62f, -9.39f).setRolloffFactor(1.4f);
        addAmbience("comms", 33.42f, 0.78f, -2.3f).setRolloffFactor(2);
        addAmbience("storage", 26.76f, 0.82f, -7.43f).setGain(1.3f);
        addAmbience("electrical", 18.55f, 0.66f, -10).setRolloffFactor(4f);
        addAmbience("admin", 34.14f, 0.7f, -13.85f).setGain(1.4f);

        Log.i("Init complete");
        if (FAST_ENTRY) {
            gameState.setCurrentState(GameState.State.Running);
        } else {
            gameState.setCurrentState(GameState.State.Menu);
            guiRenderer.setCurrentScreen(new MainMenuScreen());
        }
    }

    private SoundSource addAmbience(String name, float x, float y, float z) {
        return SoundFX.addPositional("ambience/" + name, new Vector3f(x, y, z));
    }

    private void handleSizeChange(int w, int h) {
        glViewport(0, 0, w, h);
        float aspect = (float) w / h;
        projMatrix = new Matrix4f().perspective((float) Math.toRadians(70), aspect, 0.1f, 200.0f);
        guiMatrix = new Matrix4f().ortho2D(0, w, h, 0);
        guiRenderer.onSizeChange(guiMatrix);
        if (sceneBuffer != null) {
            sceneBuffer.destroy();
            vGaussBuffer.destroy();
            hGaussBuffer.destroy();
            ssaoBuffer.destroy();
            pickBuffer.destroy();
            highlightBuffer.destroy();
            bloomBuffer.destroy();
        }

        sceneBuffer = Framebuffer.create(w, h).withDepthTexture().withColorTexture(1);
        vGaussBuffer = Framebuffer.create(w, h);
        hGaussBuffer = Framebuffer.create(w, h);
        ssaoBuffer = Framebuffer.create(w, h);
        pickBuffer = Framebuffer.create(w, h).withDepthBuffer();
        highlightBuffer = Framebuffer.create(w, h).withDepthBuffer();
        bloomBuffer = Framebuffer.create(w / 2, h / 2).withDepthBuffer();

    }

    private void selectObject(int id) {
        for (GameObject object : gameObjects) {
            object.setSelected(object.getId() == id);
        }
    }

    private int prevWidth;
    private int prevHeight;

    private void renderFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.getWidth() != prevWidth || window.getHeight() != prevHeight) {
            handleSizeChange(window.getWidth(), window.getHeight());
            this.prevWidth = window.getWidth();
            this.prevHeight = window.getHeight();
        }

        frustumCulling.update();

        if (gameState.getCurrentState() == GameState.State.Running) {
            handleControls();

            // Picking
            handlePicking();

            // Rendering
            renderSceneWithSSAO();
            renderOutlines();
        }

        renderHud();

        fps.frame();
    }

    private void renderHud() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        // Gui
        guiRenderer.render();

        // Debug
        guiRenderer.getFontRenderer().drawRightAligned(fps.get() + " fps", window.getWidth() - 5, 5, 0.25f, new Vector4f(1, 1, 1, 1));
        if (debug.isActive()) {
            guiRenderer.getFontRenderer().drawRightAligned(camera.getPosition().x + " " + camera.getPosition().y + " " + camera.getPosition().z, window.getWidth() - 5, 25, 0.25f, new Vector4f(1, 1, 1, 1));
            guiRenderer.getFontRenderer().drawRightAligned(networkTimer.getSubTicks() + " [st]", window.getWidth() - 5, 50, 0.25f, new Vector4f(1, 1, 1, 1));
        }


        // Ingame Hud
        if (gameState.isRunning()) {
            // Task progress
            final int taskHudWidth = 550;
            guiRenderer.drawRect(5, 5, taskHudWidth, 40, new Vector4f(0, 0, 0, 1));
            guiRenderer.drawRect(7, 7, taskHudWidth - 4, 40 - 4, new Vector4f(0.4f, 0.4f, 0.4f, 1));
            guiRenderer.drawRect(7, 7, (int) ((taskHudWidth - 4) * client.taskProgress), 20 - 4, new Vector4f(0.2f, 0.95f, 0.2f, 1));
            guiRenderer.getFontRenderer().draw("Task Progress", 15, 10, 0.35f, new Vector4f(1, 1, 1, 1));

            // Task list
            int y = 60;
            guiRenderer.drawRect(5, y - 5, taskHudWidth, (self.getTasks().size() + 2) * 35 - 5, new Vector4f(0.45f, 0.45f, 0.45f, 0.45f));
            String header = self.getRole() == Role.Impostor ? "Fake tasks:" : "Your tasks:";
            guiRenderer.getFontRenderer().draw(header, 20, y, 0.5f, new Vector4f(1, 1, 1, 1));
            y += 35;
            for (PlayerTask task : self.getTasks()) {
                String progress = task.isLongTask() ? " (" + task.getProgress() + "/" + task.getTasks().size() + ")" : "";
                Vector4f color = task.isDone() ? new Vector4f(0, 1, 0, 0.9f) : new Vector4f(1, 1, 1, 0.9f);
                guiRenderer.getFontRenderer().draw(task.nextTask().getRoom() + ": " + task.nextTask().getTaskType() + progress, 35, y, 0.5f, color);
                y += 35;
            }

            String prompt = self.getRole() == Role.Impostor ? "You're an IMPOSTOR" : "You're a crewmate";
            Vector4f color = self.getRole() == Role.Impostor ? new Vector4f(1, 0, 0, 1) : new Vector4f(0, 0.25f, 0.95f, 1);
            guiRenderer.getFontRenderer().drawCentered(prompt, window.getWidth() / 2f, window.getHeight() - 100f, 0.45f, color);
        }

        glEnable(GL_DEPTH_TEST);
    }

    private void renderOutlines() {
        glClear(GL_DEPTH_BUFFER_BIT);
        shadingStrategy = ShadingStrategies.FLAT;

        // Stencil
        pickBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        ShadingStrategies.FLAT.setColor(new Vector3f(1, 0, 0));
        renderSelectedObjects();
        renderHighlightedObjects();
        pickBuffer.unbind();

        // Highlight
        highlightBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        ShadingStrategies.FLAT.setColor(new Vector3f(1.0f, 1.0f, 0));
        renderSelectedObjects();
        ShadingStrategies.FLAT.setColor(new Vector3f(1.0f, 1.0f, 1.0f));
        renderHighlightedObjects();
        highlightBuffer.unbind();

        postProcessing.begin();

        // Blur the highlight
        vAvgBlurShader.bind();
        vAvgBlurShader.setTargetHeight(hGaussBuffer.getHeight());
        postProcessing.bindTexture(0, highlightBuffer.getColorTexture(0));
        postProcessing.copyTo(hGaussBuffer);

        hAvgBlurShader.bind();
        hAvgBlurShader.setTargetWidth(highlightBuffer.getWidth());
        postProcessing.bindTexture(0, hGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(highlightBuffer);

        // Now, copy highlight to screen
        copyShader.bind();
        postProcessing.bindTexture(0, highlightBuffer.getColorTexture(0));
        postProcessing.bindTexture(1, pickBuffer.getColorTexture(0));
        postProcessing.renderTo(null);

        postProcessing.end();
    }

    private void renderSelectedObjects() {

        for (GameObject o : gameObjects)
            if (o.isSelected()) {
                o.render(RenderLayer.Base);
                o.render(RenderLayer.Transparency);
            }
    }

    private void renderHighlightedObjects() {
        for (GameObject o : gameObjects)
            if (o.isHighlighted() && o.getPosition().distance(camera.getPosition()) < 10) {
                o.render(RenderLayer.Base);
                o.render(RenderLayer.Transparency);
            }
    }

    private void handlePicking() {
        ShadingStrategies.FLAT.setColor(new Vector3f(0, 0, 0));
        pickBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        for (GameObject go : gameObjects)
            if (go.canPlayerInteract() && go.getPosition().distance(camera.getPosition()) < 2) {
                shadingStrategy = ShadingStrategies.PICK;
                go.render(RenderLayer.Base);
            } else {
                shadingStrategy = ShadingStrategies.FLAT;
                go.render(RenderLayer.Base);
            }


        pickedBytes.clear();
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glReadPixels(pickBuffer.getWidth() / 2, pickBuffer.getHeight() / 2, 1, 1, GL_RGB, GL_UNSIGNED_BYTE, pickedBytes);

        int reconstructedId = (pickedBytes.get(0) & 0xFF) | ((pickedBytes.get(1) & 0xFF) << 8);
        selectObject(reconstructedId);

        pickBuffer.unbind();
    }

    private void renderSceneWithSSAO() {
        shadingStrategy = ShadingStrategies.DEFAULT;
        sceneBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderScene();
        shadingStrategy = ShadingStrategies.FLAT;
        ShadingStrategies.FLAT.setColor(new Vector3f(1.0f, 1.0f, 0));
        debug.render();
        sceneBuffer.unbind();

        bloomBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shadingStrategy = ShadingStrategies.BLOOM;
        renderScene();
        bloomBuffer.unbind();

        postProcessing.begin();

        // SSAO
        ssaoShader.bind();
        ssaoShader.setNoiseScale(window.getWidth() / 10f);
        postProcessing.bindTexture(0, sceneBuffer.getColorTexture(0)); // Color
        postProcessing.bindTexture(1, sceneBuffer.getDepthTexture()); // Depth
        postProcessing.bindTexture(2, sceneBuffer.getColorTexture(1)); // Normals
        postProcessing.bindTexture(3, ssaoNoiseTexture); // Noise
        postProcessing.copyTo(vGaussBuffer);

        vGaussShader.bind();
        vGaussShader.setTargetHeight(hGaussBuffer.getHeight());
        postProcessing.bindTexture(0, vGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(hGaussBuffer);

        hGaussShader.bind();
        hGaussShader.setTargetWidth(ssaoBuffer.getWidth());
        postProcessing.bindTexture(0, hGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(ssaoBuffer);

        // Bloom
        vGaussShader.bind();
        vGaussShader.setTargetHeight(vGaussBuffer.getHeight());
        postProcessing.bindTexture(0, bloomBuffer.getColorTexture(0));
        postProcessing.copyTo(vGaussBuffer);
        hGaussShader.bind();
        hGaussShader.setTargetWidth(bloomBuffer.getHeight());
        postProcessing.bindTexture(0, vGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(bloomBuffer);

        mulShader.bind();
        postProcessing.bindTexture(0, sceneBuffer.getColorTexture(0));
        postProcessing.bindTexture(1, ssaoBuffer.getColorTexture(0));
        postProcessing.bindTexture(2, bloomBuffer.getColorTexture(0));
        postProcessing.copyTo(null);

        postProcessing.end();
    }

    private void renderScene() {
        for (GameObject go : gameObjects)
            go.render(RenderLayer.Base);
        for (GameObject go : gameObjects)
            go.render(RenderLayer.Transparency);

        shadingStrategy = ShadingStrategies.FLAT;

        for (Player player : client.users.values()) {
            if (!player.getUsername().equals(self.getUsername()) && !player.isDead() && player.getPosition() != null) {
                ShadingStrategies.FLAT.setColor(player.getColor().toVector());
                Vector3f oldPos = player.getLastTickPosition();
                Vector3f newPos = player.getNextTickPosition();
                float ipX = oldPos.x + (newPos.x - oldPos.x) * networkTimer.getSubTicks();
                float ipY = oldPos.y + (newPos.y - oldPos.y) * networkTimer.getSubTicks();
                float ipZ = oldPos.z + (newPos.z - oldPos.z) * networkTimer.getSubTicks();
                float ipR = player.getLastTickRotation() + (player.getNextTickRotation() - player.getLastTickRotation()) * networkTimer.getSubTicks();

                playerModel.render(new Matrix4f()
                        .rotateLocal(ipR, 0, 1, 0)
                        .scaleLocal(0.18f)
                        .translateLocal(ipX, ipY, ipZ));
            }
        }

    }

    private void handleControls() {


        if (networkTimer.elapsed() && gameState.getCurrentState() == GameState.State.Running) {
            networkTimer.reset();
            NetMessage.PlayerMove msg = new NetMessage.PlayerMove();
            msg.angle = (float) Math.toRadians(camera.getAngle().x);
            msg.x = camera.getPosition().x;
            msg.y = camera.getPosition().y;
            msg.z = camera.getPosition().z;
            client.sendMessage(msg);
            for (Player player : client.users.values())
                player.updatePosition();
        }

        if (!updateTimer.elapsed())
            return;
        updateTimer.reset();

        float speed = 0.04f;

        float yaw = (float) Math.toRadians(camera.getAngle().x);

        float dx = (float) Math.sin(yaw) * speed;
        float dz = (float) Math.cos(yaw) * speed;

        float dx2 = (float) Math.sin(yaw + Math.PI / 2) * speed;
        float dz2 = (float) Math.cos(yaw + Math.PI / 2) * speed;

        if (window.isKeyPressed(GLFW_KEY_W))
            camera.getPosition().add(new Vector3f(dx, 0.0f, dz));

        if (window.isKeyPressed(GLFW_KEY_A))
            camera.getPosition().add(new Vector3f(dx2, 0.0f, dz2));

        if (window.isKeyPressed(GLFW_KEY_S))
            camera.getPosition().sub(new Vector3f(dx, 0.0f, dz));

        if (window.isKeyPressed(GLFW_KEY_D))
            camera.getPosition().sub(new Vector3f(dx2, 0.0f, dz2));

        if (debug.isActive()) {
            if (window.isKeyPressed(GLFW_KEY_SPACE))
                camera.getPosition().add(new Vector3f(0, speed, 0));

            if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
                camera.getPosition().add(new Vector3f(0, -speed, 0));
        } else if (!self.isDead())
            shipCollider.updatePlayerLocation(camera.getPosition());

        Vector2f pos = window.getCursorPosition();
        Vector2f delta = pos.sub(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));
        camera.getAngle().add(new Vector2f(-delta.x * 0.04f, -delta.y * 0.04f));
        window.setCursorPosition(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));


        if (camera.getAngle().y > 90) camera.getAngle().y = (float) 90;
        if (camera.getAngle().y < -90) camera.getAngle().y = (float) -90;

        viewMatrix = camera.calcViewMatrix();
        OpenAL.update();
    }

    public GameWindow getWindow() {
        return window;
    }

    public Camera getCamera() {
        return camera;
    }

    public ShaderProvider getShaderProvider() {
        return shaderProvider;
    }

    public TextureProvider getTextureProvider() {
        return textureProvider;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public ShadingStrategy getShadingStrategy() {
        return shadingStrategy;
    }

    public Debug getDebug() {
        return debug;
    }

    public void setShadingStrategy(ShadingStrategy shadingStrategy) {
        this.shadingStrategy = shadingStrategy;
    }

    public TaskGameObject findTask(Room room, TaskType type) {
        for (GameObject object : gameObjects) {
            if (object instanceof TaskGameObject) {
                TaskGameObject task = (TaskGameObject) object;
                if (task.getRoom() == room && task.getTaskType() == type)
                    return task;
            }
        }
        return null;
    }

    public Player getSelf() {
        return self;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Matrix4f getGuiMatrix() {
        return guiMatrix;
    }

    public AmongUsClient getClient() {
        return client;
    }

    public GuiRenderer getGuiRenderer() {
        return guiRenderer;
    }

    public SoundProvider getSoundProvider() {
        return soundProvider;
    }

    public FrustumCulling getFrustumCulling() {
        return frustumCulling;
    }

    public float getVision() {
        if (self.isDead())
            return VISION_GHOST;

        if (client.currentSabotage == Sabotage.Lights)
            return VISION_SABOTAGED;
        else if (self.getRole() == Role.Impostor)
            return VISION_IMPOSTOR;
        else if (self.getRole() == Role.Crewmate)
            return VISION_CREWMATE;

        Log.e("Invalid vision condition");
        return 0;
    }
}
