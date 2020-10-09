package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShaderPick extends Shader {

    private int loc_viewMatrix;
    private int loc_projMatrix;
    private int loc_modelMatrix;
    private int loc_modelId;

    public ShaderPick() {
        super("shaders/base.v.glsl", "shaders/pick.f.glsl");
    }

    @Override
    public void bindUniforms() {
        loc_viewMatrix = getLocation("viewMatrix");
        loc_projMatrix = getLocation("projMatrix");
        loc_modelMatrix = getLocation("modelMatrix");
        loc_modelId = getLocation("modelId");
    }

    public void setModelMatrix(Matrix4f mat) {
        setMatrix(loc_modelMatrix, mat);
    }

    public void setViewMatrix(Matrix4f mat) {
        setMatrix(loc_viewMatrix, mat);
    }

    public void setProjMatrix(Matrix4f mat) {
        setMatrix(loc_projMatrix, mat);
    }

    public void setModelId(int modelId) {
        setInt(loc_modelId, modelId);
    }
}
