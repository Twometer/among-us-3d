package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class ShaderSSAO extends Shader {

    // thank u <3    http://theorangeduck.com/page/pure-depth-ssao

    public ShaderSSAO() {
        super("shaders/post_ssao.v.glsl", "shaders/post_ssao.f.glsl");
    }

    @Override
    public void bindUniforms() {
        glUniform1i(glGetUniformLocation(id, "colorSampler"), 0);
        glUniform1i(glGetUniformLocation(id, "depthSampler"), 1);
    }
}
