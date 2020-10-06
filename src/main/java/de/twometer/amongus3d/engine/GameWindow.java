package de.twometer.amongus3d.engine;

import de.twometer.amongus3d.core.ILifecycle;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
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

    public GameWindow(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
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
        glfwWindowHint(GLFW_SAMPLES, 8);

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


}
