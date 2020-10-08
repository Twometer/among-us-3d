package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

public class ShaderVGauss extends Shader {

    private int loc_targetHeight;

    public ShaderVGauss() {
        super("shaders/post_vgauss.v.glsl", "shaders/post_vgauss.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_targetHeight = getLocation("targetHeight");
    }

    public void setTargetHeight(float height) {
        setFloat(loc_targetHeight, height);
    }
}
