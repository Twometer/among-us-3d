package de.twometer.amongus3d.core;

import de.twometer.amongus3d.io.MapLoader;
import de.twometer.amongus3d.obj.GameObject;
import de.twometer.amongus3d.postproc.PostProcessing;
import de.twometer.amongus3d.postproc.SSAO;
import de.twometer.amongus3d.render.*;
import de.twometer.amongus3d.render.shaders.ShaderHGauss;
import de.twometer.amongus3d.render.shaders.ShaderMul;
import de.twometer.amongus3d.render.shaders.ShaderSSAO;
import de.twometer.amongus3d.render.shaders.ShaderVGauss;
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
    private Matrix4f guiMatrix;

    private final PostProcessing postProcessing = new PostProcessing();
    private int ssaoNoiseTexture;
    private ShaderSSAO ssaoShader;
    private ShaderVGauss vGaussShader;
    private ShaderHGauss hGaussShader;
    private ShaderMul mulShader;
    private Framebuffer sceneBuffer;
    private Framebuffer vGaussBuffer;
    private Framebuffer hGaussBuffer;
    private Framebuffer ssaoBuffer;


    private Game() {
    }

    public static Game instance() {
        return gameInstance;
    }

    public void run() {
        window.create();
        window.setCursorVisible(false);
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
            handleSizeChange(width, height);
        });
        handleSizeChange(window.getWidth(), window.getHeight());

        for (GameObject object : gameObjects)
            object.init();

        ssaoShader = shaderProvider.getShader(ShaderSSAO.class);
        vGaussShader = shaderProvider.getShader(ShaderVGauss.class);
        hGaussShader = shaderProvider.getShader(ShaderHGauss.class);
        mulShader = shaderProvider.getShader(ShaderMul.class);
        postProcessing.initialize();


    }

    private void handleSizeChange(int w, int h) {
        float aspect = (float) w / h;
        projMatrix = new Matrix4f().perspective((float) Math.toRadians(70), aspect, 0.1f, 200.0f);
        guiMatrix = new Matrix4f().ortho2D(0, w, h, 0);

        if (sceneBuffer != null) {
            sceneBuffer.destroy();
            vGaussBuffer.destroy();
            hGaussBuffer.destroy();
            ssaoBuffer.destroy();
        }

        sceneBuffer = Framebuffer
                .create(w, h)
                .withDepthTexture()
                .withColorTexture(1);

        vGaussBuffer = Framebuffer.create(w, h);
        hGaussBuffer = Framebuffer.create(w, h);
        ssaoBuffer = Framebuffer.create(w, h);

        glDeleteTextures(ssaoNoiseTexture);

        ssaoNoiseTexture = SSAO.createNoiseTexture(512, (int) (512 * aspect));
    }

    private void renderFrame() {
        handleControls();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        sceneBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderScene();
        sceneBuffer.unbind();

        postProcessing.begin();

        ssaoShader.bind();
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
        postProcessing.bindTexture(1, hGaussBuffer.getColorTexture(0));
        postProcessing.copyTo(null);

        postProcessing.end();

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

        /*if (window.isKeyPressed(GLFW_KEY_SPACE))
            camera.getPosition().add(new Vector3f(0f, 0.1f, 0f));

        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            camera.getPosition().add(new Vector3f(0f, -0.1f, 0f));*/

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
