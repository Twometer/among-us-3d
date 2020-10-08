package de.twometer.amongus3d.mesh;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.shading.ShadingStrategy;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Renderable {

    private int modelId;

    private static final Matrix4f IDENTITY = new Matrix4f();

    public void render() {
        render(IDENTITY);
    }

    public abstract void render(Matrix4f modelMatrix);

    public abstract Vector3f getCenterOfMass();

    ShadingStrategy getShadingStrategy() {
        return Game.instance().getShadingStrategy();
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
}
