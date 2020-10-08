package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

public class ShaderVAvgBlur extends Shader {

    private int loc_targetHeight;

    public ShaderVAvgBlur() {
        super("shaders/post_avgblurv.v.glsl", "shaders/post_avgblur.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_targetHeight = getLocation("targetHeight");
    }

    public void setTargetHeight(float height) {
        setFloat(loc_targetHeight, height);
    }
}
