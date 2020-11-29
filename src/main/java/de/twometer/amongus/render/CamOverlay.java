package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
import de.twometer.neko.render.overlay.IOverlay;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class CamOverlay implements IOverlay {

    private final CamShader shader;

    public CamOverlay() {
        shader = AmongUs.get().getShaderProvider().getShader(CamShader.class);
    }

    @Override
    public void setupShader() {
        shader.bind();
        shader.time.set((float) glfwGetTime());
    }
}
