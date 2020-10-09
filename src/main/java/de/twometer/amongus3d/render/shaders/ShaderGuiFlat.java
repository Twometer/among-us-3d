package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShaderGuiFlat extends Shader {

    private int loc_projectionMatrix;
    private int loc_transformationMatrix;
    private int loc_color;

    public ShaderGuiFlat() {
        super("shaders/gui.v.glsl", "shaders/gui_flat.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_projectionMatrix = getLocation("projectionMatrix");
        loc_transformationMatrix = getLocation("transformationMatrix");
        loc_color = getLocation("flatColor");
    }

    public void setColor(Vector4f color) {
        setVector4(loc_color, color);
    }

    public void setTransformationMatrix(Matrix4f matrix) {
        setMatrix(loc_transformationMatrix, matrix);
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        setMatrix(loc_projectionMatrix, matrix);
    }



}
