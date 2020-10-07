package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.AnimationType;
import de.twometer.amongus3d.render.RenderLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StaticAnimGameObject extends StaticGameObject {

    private Vector3f centerOfMass;

    private final AnimationType type;

    private float angle = 0;

    public StaticAnimGameObject(String name, Renderable model, AnimationType type) {
        super(name, model);
        this.type = type;
    }

    @Override
    public void init() {
        centerOfMass = model.getCenterOfMass();
    }

    @Override
    public void render(RenderLayer layer) {
        if (layer != RenderLayer.Base)
            return;

        Matrix4f mat = new Matrix4f();
        mat.translate(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z);
        mat.rotateLocal(angle, 0,0,1);
        mat.translateLocal(centerOfMass.x, centerOfMass.y, centerOfMass.z);

        model.render(mat);

        angle += type == AnimationType.Spin ? 0.5 : 0.01;
    }

    @Override
    public String toString() {
        return "ANIM." + type;
    }
}
