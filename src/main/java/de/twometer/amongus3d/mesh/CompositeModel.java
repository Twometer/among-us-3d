package de.twometer.amongus3d.mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CompositeModel extends Renderable {

    private final List<Model> models;

    public CompositeModel(List<Model> models) {
        this.models = new ArrayList<>(models);
    }

    @Override
    public void render(Matrix4f mat) {
        for (Model model : models)
            model.render(mat);
    }

    @Override
    public void setModelId(int modelId) {
        super.setModelId(modelId);
        for (Model model : models)
            model.setModelId(getModelId());
    }

    @Override
    public Vector3f getCenterOfMass() {
        float x = 0;
        float y = 0;
        float z = 0;

        for (Model model : models) {
            Vector3f com = model.getCenterOfMass();
            x += com.x;
            y += com.y;
            z += com.z;
        }

        float num = models.size();
        return new Vector3f(x / num, y / num, z / num);
    }

}
