package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

public class ShaderHAvgBlur extends Shader {

    private int loc_targetWidth;

    public ShaderHAvgBlur() {
        super("shaders/post_avgblurh.v.glsl", "shaders/post_avgblur.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_targetWidth = getLocation("targetWidth");
    }

    public void setTargetWidth(float height) {
        setFloat(loc_targetWidth, height);
    }
}
