package de.twometer.amongus3d.render.shaders;

public class ShaderSimpleTextured extends ShaderSimple {

    private int loc_texSampler;

    public ShaderSimpleTextured() {
        super("shaders/simple.v.glsl", "shaders/simpleTextured.f.glsl");
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
