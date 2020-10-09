package de.twometer.amongus3d.core;

import de.twometer.amongus3d.client.AmongUsClient;
import de.twometer.amongus3d.io.ColliderLoader;
import de.twometer.amongus3d.io.MapLoader;
import de.twometer.amongus3d.mesh.shading.ShadingStrategies;
import de.twometer.amongus3d.mesh.shading.ShadingStrategy;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.PlayerColor;
import de.twometer.amongus3d.model.player.Role;
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
import de.twometer.amongus3d.ui.MainMenuScreen;
import de.twometer.amongus3d.util.Debug;
import de.twometer.amongus3d.util.Fps;
import de.twometer.amongus3d.util.Log;
import de.twometer.amongus3d.util.Timer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
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

    private static final boolean DEBUG_MODE = false;

    public static final String NAME = "Among Us 3D";
    public static final String VERSION = "beta-0.1";

    private final GameWindow window = new GameWindow(NAME + " " + VERSION + (DEBUG_MODE ? " [Debug]" : ""), 1024, 768);
    private final Timer updateTimer = new Timer(90);
    private final ShaderProvider shaderProvider = new ShaderProvider();
    private final TextureProvider textureProvider = new TextureProvider();
    private final Camera camera = new Camera();
    private final Fps fps = new Fps();
    private final Debug debug = new Debug();

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
        window.setSizeCallback((width, height) -> {
            glViewport(0, 0, width, height);
            handleSizeChange(width, height);
        });
        window.setClickCallback(button -> {
            for (GameObject object : gameObjects)
                if (object.isSelected())
                    object.onClicked();

            guiRenderer.onClick((int) window.getCursorPosition().x, (int) window.getCursorPosition().y);
        });
        window.setCharTypedCallback(guiRenderer::onCharTyped);
        handleSizeChange(window.getWidth(), window.getHeight());

        //// PRE-INIT ////
        gameState.setCurrentState(GameState.State.Loading);
        self = new Player("Debug", camera.getPosition(), Role.Crewmate, PlayerColor.Blue);

        //// LOADING SCREEN ////
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        guiRenderer.init();
        guiRenderer.render();
        window.update();

        //// INIT OBJECTS ////

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

        gameState.setCurrentState(GameState.State.Menu);
        guiRenderer.setCurrentScreen(new MainMenuScreen());
    }

    private void handleSizeChange(int w, int h) {
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
        }

        sceneBuffer = Framebuffer.create(w, h).withDepthTexture().withColorTexture(1);
        vGaussBuffer = Framebuffer.create(w, h);
        hGaussBuffer = Framebuffer.create(w, h);
        ssaoBuffer = Framebuffer.create(w, h);
        pickBuffer = Framebuffer.create(w, h).withDepthBuffer();
        highlightBuffer = Framebuffer.create(w, h).withDepthBuffer();

        glDeleteTextures(ssaoNoiseTexture);

        ssaoNoiseTexture = SSAO.createNoiseTexture(10, 10);
    }

    private void selectObject(int id) {
        for (GameObject object : gameObjects) {
            object.setSelected(object.getId() == id);
        }
    }

    private void renderFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (gameState.getCurrentState() == GameState.State.Running) {
            handleControls();

            // Picking
            handlePicking();

            // Rendering
            renderSceneWithSSAO();
            renderOutlines();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        guiRenderer.render();

        fps.frame();
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

        postProcessing.begin();

        ssaoShader.bind();
        ssaoShader.setNoiseScale(window.getWidth() / 10f);
        postProcessing.bindTexture(0, sceneBuffer.getColorTexture(0)); // Color
        postProcessing.bindTexture(1, sceneBuffer.getDepthTexture()); // Depth
        postProcessing.bindTexture(2, sceneBuffer.getColorTexture(1)); // Normals
        postProcessing.bindTexture(3, ssaoNoiseTexture); // Noise
        postProcessing.copyTo(vGaussBuffer);

        vGaussShader.bind();
        vGaussShader.setTargetHeight(vGaussBuffer.getHeight());
        postProcessing.bindTexture(0, vGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(hGaussBuffer);

        hGaussShader.bind();
        hGaussShader.setTargetWidth(hGaussBuffer.getWidth());
        postProcessing.bindTexture(0, hGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(ssaoBuffer);

        mulShader.bind();
        postProcessing.bindTexture(0, sceneBuffer.getColorTexture(0));
        postProcessing.bindTexture(1, ssaoBuffer.getColorTexture(0));
        postProcessing.copyTo(null);

        postProcessing.end();
    }

    private void renderScene() {
        for (GameObject go : gameObjects)
            go.render(RenderLayer.Base);

        for (GameObject go : gameObjects)
            go.render(RenderLayer.Glow);

        for (GameObject go : gameObjects)
            go.render(RenderLayer.Transparency);
    }

    private void handleControls() {
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
        } else
            shipCollider.updatePlayerLocation(camera.getPosition());

        Vector2f pos = window.getCursorPosition();
        Vector2f delta = pos.sub(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));
        camera.getAngle().add(new Vector2f(-delta.x * 0.04f, -delta.y * 0.04f));
        window.setCursorPosition(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));


        if (camera.getAngle().y > 90) camera.getAngle().y = (float) 90;
        if (camera.getAngle().y < -90) camera.getAngle().y = (float) -90;

        viewMatrix = camera.calcViewMatrix();
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
}
