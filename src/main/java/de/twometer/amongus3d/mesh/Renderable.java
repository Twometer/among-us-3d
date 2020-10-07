package de.twometer.amongus3d.mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Renderable {

    private static final Matrix4f IDENTITY = new Matrix4f();

    public void render() {
        render(IDENTITY);
    }

    public abstract void render(Matrix4f modelMatrix);

    public abstract Vector3f getCenterOfMass();

}
