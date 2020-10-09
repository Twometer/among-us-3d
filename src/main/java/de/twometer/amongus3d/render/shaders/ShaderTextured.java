package de.twometer.amongus3d.render.shaders;

public class ShaderTextured extends ShaderFlat {

    private int loc_texSampler;

    public ShaderTextured() {
        super("shaders/base.v.glsl", "shaders/base_tex.f.glsl");
    }

    @Override
    public void bindUniforms() {
        super.bindUniforms();
        loc_texSampler = getLocation("texSampler");
    }


    public void setTexSampler(int sampler) {
        setInt(loc_texSampler, sampler);
    }
}
