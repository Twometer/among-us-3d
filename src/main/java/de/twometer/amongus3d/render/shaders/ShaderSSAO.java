package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

import static org.lwjgl.opengl.GL20.*;

public class ShaderSSAO extends Shader {

    private int loc_noiseScale;

    public ShaderSSAO() {
        super("shaders/post_ssao.v.glsl", "shaders/post_ssao.f.glsl");
    }

    @Override
    public void bindUniforms() {
        glUniform1i(glGetUniformLocation(id, "colorSampler"), 0);
        glUniform1i(glGetUniformLocation(id, "depthSampler"), 1);
        glUniform1i(glGetUniformLocation(id, "normalSampler"), 2);
        glUniform1i(glGetUniformLocation(id, "randomSampler"), 3);
        loc_noiseScale = getLocation("noiseScale");
    }


    public void setNoiseScale(float noiseScale) {
        setFloat(loc_noiseScale, noiseScale);
    }
}

