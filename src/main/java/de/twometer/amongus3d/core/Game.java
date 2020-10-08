package de.twometer.amongus3d.core;

import de.twometer.amongus3d.io.ColliderLoader;
import de.twometer.amongus3d.io.MapLoader;
import de.twometer.amongus3d.mesh.shading.ShadingStrategies;
import de.twometer.amongus3d.mesh.shading.ShadingStrategy;
import de.twometer.amongus3d.obj.GameObject;
import de.twometer.amongus3d.phys.Collider;
import de.twometer.amongus3d.postproc.PostProcessing;
import de.twometer.amongus3d.postproc.SSAO;
import de.twometer.amongus3d.render.*;
import de.twometer.amongus3d.render.shaders.*;
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

public class Game {

    private static final Game gameInstance = new Game();

    private final GameWindow window = new GameWindow("Among Us 3D", 1024, 768);
    private final Timer updateTimer = new Timer(90);
    private final ShaderProvider shaderProvider = new ShaderProvider();
    private final TextureProvider textureProvider = new TextureProvider();
    private final Camera camera = new Camera();
    private final Fps fps = new Fps();

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

    private final ByteBuffer pickedBytes = BufferUtils.createByteBuffer(3);

    private ShadingStrategy shadingStrategy = ShadingStrategies.DEFAULT;

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

        gameObjects.addAll(MapLoader.loadMap("models/the_skeld.obj"));
        shipCollider = ColliderLoader.loadCollider("models/collider.obj");
        shipCollider.prepareDebugRender();

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
        vAvgBlurShader = shaderProvider.getShader(ShaderVAvgBlur.class);
        hAvgBlurShader = shaderProvider.getShader(ShaderHAvgBlur.class);
        mulShader = shaderProvider.getShader(ShaderMul.class);
        copyShader = shaderProvider.getShader(ShaderCopy.class);
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

        ssaoNoiseTexture = SSAO.createNoiseTexture((int) (1024 * aspect), 1024);
    }

    private void selectObject(int id) {
        for (GameObject object : gameObjects) {
            object.setSelected(object.getId() == id);
        }
    }

    private void renderFrame() {
        handleControls();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Picking
        handlePicking();

        // Rendering
        renderSceneWithSSAO();
        glClear(GL_DEPTH_BUFFER_BIT);
        renderHighlight();

        fps.frame();
    }

    private void renderHighlight() {
        shadingStrategy = ShadingStrategies.HIGHLIGHT;

        // Stencil
        pickBuffer.bind();
        ShadingStrategies.HIGHLIGHT.setHighlightColor(new Vector3f(1, 0, 0));
        renderSelectedObjects();
        pickBuffer.unbind();

        // Highlight
        highlightBuffer.bind();
        ShadingStrategies.HIGHLIGHT.setHighlightColor(new Vector3f(1.0f, 1.0f, 0));
        renderSelectedObjects();
        highlightBuffer.unbind();

        postProcessing.begin();


        // Blur the highlight
        vAvgBlurShader.bind();
        vAvgBlurShader.setTargetHeight(vGaussBuffer.getHeight());
        postProcessing.bindTexture(0, highlightBuffer.getColorTexture(0));
        postProcessing.copyTo(hGaussBuffer);

        hAvgBlurShader.bind();
        hAvgBlurShader.setTargetWidth(hGaussBuffer.getWidth());
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        for (GameObject o : gameObjects)
            if (o.isSelected()) {
                o.render(RenderLayer.Base);
                o.render(RenderLayer.Transparency);
            }
    }

    private void handlePicking() {
        shadingStrategy = ShadingStrategies.PICK;
        pickBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        for (GameObject go : gameObjects)
            if (go.canPlayerInteract() /*&& go.getPosition().distance(camera.getPosition()) < 3*/)
                go.render(RenderLayer.Base);

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
        shadingStrategy = ShadingStrategies.HIGHLIGHT;
       // shipCollider.renderDebug();
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
}
