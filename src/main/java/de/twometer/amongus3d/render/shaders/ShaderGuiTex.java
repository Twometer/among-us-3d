package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShaderGuiTex extends Shader {

    private int loc_projectionMatrix;
    private int loc_offset;

    public ShaderGuiTex() {
        super("shaders/gui.v.glsl", "shaders/gui_tex.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_projectionMatrix = getLocation("projectionMatrix");
        loc_offset = getLocation("offset");
    }

    public void setOffset(Vector3f offset) {
        setVector3(loc_offset, offset);
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        setMatrix(loc_projectionMatrix, matrix);
    }

}
