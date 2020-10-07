package de.twometer.amongus3d.core;

import de.twometer.amongus3d.io.MapLoader;
import de.twometer.amongus3d.obj.GameObject;
import de.twometer.amongus3d.render.Camera;
import de.twometer.amongus3d.render.RenderLayer;
import de.twometer.amongus3d.render.ShaderProvider;
import de.twometer.amongus3d.render.TextureProvider;
import de.twometer.amongus3d.util.Fps;
import de.twometer.amongus3d.util.Log;
import de.twometer.amongus3d.util.Timer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {

    private static final Game gameInstance = new Game();

    private final GameWindow window = new GameWindow("Among Us 3D", 1024, 768);
    private final Timer updateTimer = new Timer(90);
    private final ShaderProvider shaderProvider = new ShaderProvider();
    private final TextureProvider textureProvider = new TextureProvider();
    private final Camera camera = new Camera();
    private final Fps fps = new Fps();

    private final List<GameObject> gameObjects = new ArrayList<>();
    private Matrix4f viewMatrix;
    private Matrix4f projMatrix;

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

        gameObjects.addAll(MapLoader.loadMap("models\\the_skeld.obj"));

        Log.i("Loaded " + gameObjects.size() + " game objects.");

        window.setSizeCallback((width, height) -> {
            glViewport(0, 0, width, height);
            recalculateMatrix(width, height);
        });
        recalculateMatrix(window.getWidth(), window.getHeight());

        for (GameObject object : gameObjects)
            object.init();
    }

    private void recalculateMatrix(int w, int h) {
        projMatrix = new Matrix4f().perspective((float) Math.toRadians(70), (float) w / h, 0.1f, 200.0f);
    }

    private void renderFrame() {
        handleControls();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderScene();
        fps.frame();
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

        float yaw = (float) Math.toRadians(camera.getAngle().x);

        float dx = (float) Math.sin(yaw) * 0.1f;
        float dz = (float) Math.cos(yaw) * 0.1f;

        float dx2 = (float) Math.sin(yaw + Math.PI / 2) * 0.1f;
        float dz2 = (float) Math.cos(yaw + Math.PI / 2) * 0.1f;

        if (window.isKeyPressed(GLFW_KEY_W))
            camera.getPosition().add(new Vector3f(dx, 0.0f, dz));

        if (window.isKeyPressed(GLFW_KEY_A))
            camera.getPosition().add(new Vector3f(dx2, 0.0f, dz2));

        if (window.isKeyPressed(GLFW_KEY_S))
            camera.getPosition().sub(new Vector3f(dx, 0.0f, dz));

        if (window.isKeyPressed(GLFW_KEY_D))
            camera.getPosition().sub(new Vector3f(dx2, 0.0f, dz2));

        if (window.isKeyPressed(GLFW_KEY_SPACE))
            camera.getPosition().add(new Vector3f(0f, 0.1f, 0f));

        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            camera.getPosition().add(new Vector3f(0f, -0.1f, 0f));

        Vector2f pos = window.getCursorPosition();
        Vector2f delta = pos.sub(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));
        camera.getAngle().add(new Vector2f(-delta.x * 0.04f, -delta.y * 0.04f));
        window.setCursorPosition(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));

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
}
