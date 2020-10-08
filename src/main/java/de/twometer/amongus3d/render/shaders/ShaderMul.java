package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class ShaderMul extends Shader {
    public ShaderMul() {
        super("shaders/post_mul.v.glsl", "shaders/post_mul.f.glsl");
    }

    @Override
    public void bindUniforms() {
        glUniform1i(glGetUniformLocation(id, "aSampler"), 0);
        glUniform1i(glGetUniformLocation(id, "bSampler"), 1);
    }
}
