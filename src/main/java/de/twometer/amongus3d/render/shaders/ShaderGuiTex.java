package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShaderGuiTex extends Shader {

    private int loc_projectionMatrix;
    private int loc_transformationMatrix;

    public ShaderGuiTex() {
        super("shaders/gui.v.glsl", "shaders/gui_tex.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_projectionMatrix = getLocation("projectionMatrix");
        loc_transformationMatrix = getLocation("offset");
    }

    public void setTransformationMatrix(Matrix4f matrix) {
        setMatrix(loc_transformationMatrix, matrix);
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        setMatrix(loc_projectionMatrix, matrix);
    }

}
