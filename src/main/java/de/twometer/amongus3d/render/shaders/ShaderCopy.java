package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class ShaderCopy extends Shader {
    public ShaderCopy() {
        super("shaders/post_copy.v.glsl", "shaders/post_copy.f.glsl");
    }

    @Override
    public void bindUniforms() {
        glUniform1i(glGetUniformLocation(id, "sampler"), 0);
        glUniform1i(glGetUniformLocation(id, "samplerStencil"), 1);
    }
}
