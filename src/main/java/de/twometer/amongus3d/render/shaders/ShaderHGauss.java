package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;

public class ShaderHGauss extends Shader {

    private int loc_targetWidth;

    public ShaderHGauss() {
        super("shaders/post_hgauss.v.glsl", "shaders/post_hgauss.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_targetWidth = getLocation("targetWidth");
    }

    public void setTargetWidth(float height) {
        setFloat(loc_targetWidth, height);
    }
}
