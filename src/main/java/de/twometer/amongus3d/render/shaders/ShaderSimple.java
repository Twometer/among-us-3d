package de.twometer.amongus3d.render.shaders;

import org.joml.Matrix4f;

public class ShaderSimple extends Shader {

    private int loc_viewMatrix;
    private int loc_projMatrix;

    public ShaderSimple() {
        super("shaders/simple.v.glsl", "shaders/simple.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_viewMatrix = getLocation("viewMatrix");
        loc_projMatrix = getLocation("projMatrix");
    }

    public void setViewMatrix(Matrix4f mat) {
        setMatrix(loc_viewMatrix, mat);
    }

    public void setProjMatrix(Matrix4f mat) {
        setMatrix(loc_projMatrix, mat);
    }

}
