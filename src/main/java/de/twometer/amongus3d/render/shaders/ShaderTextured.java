package de.twometer.amongus3d.render.shaders;

public class ShaderTextured extends ShaderSimple {

    private int loc_texSampler;

    public ShaderTextured() {
        super("shaders/simple.v.glsl", "shaders/textured.f.glsl");
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
