package de.twometer.amongus3d.core;

import de.twometer.amongus3d.core.ILifecycle;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow implements ILifecycle {

    private long handle;

    private final String title;

    private int width;

    private int height;

    private float scale;

    public SizeCallback sizeCallback;

    public interface SizeCallback {
        void sizeChanged(int width, int height);
    }

    public GameWindow(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void setSizeCallback(SizeCallback sizeCallback) {
        this.sizeCallback = sizeCallback;
    }

    @Override
    public void create() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new IllegalStateException("Failed to create GLFW window");

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        float[] scaleBuf = new float[1];
        glfwGetWindowContentScale(handle, scaleBuf, scaleBuf);
        scale = scaleBuf[0];

        if (scale > 1.0f)
            setSize((int) (width * scale), (int) (height * scale));

        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            if (sizeCallback != null)
                sizeCallback.sizeChanged(width, height);
            this.width = width;
            this.height = height;
        });
    }

    @Override
    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    @Override
    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public float getScale() {
        return scale;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void setCursorPosition(Vector2f vec) {
        glfwSetCursorPos(handle, vec.x, vec.y);
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    public Vector2f getCursorPosition() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        glfwGetCursorPos(handle, xPos, yPos);
        return new Vector2f((float) xPos[0], (float) yPos[0]);
    }

    private void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        glfwSetWindowSize(handle, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
