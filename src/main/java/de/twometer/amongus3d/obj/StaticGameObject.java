package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.render.RenderLayer;
import org.joml.Vector3f;

public class StaticGameObject extends GameObject {

    final Renderable model;

    public StaticGameObject(String name, Renderable model) {
        super(name);
        this.model = model;
    }

    @Override
    public void init() {
        super.init();
        model.setModelId(getId());
        Game.instance().getDebug().addDebugPos(model.getCenterOfMass());
    }

    @Override
    public void render(RenderLayer layer) {
        if (layer != RenderLayer.Base)
            return;
        model.render();
    }

    @Override
    public boolean canPlayerInteract() {
        return false;
    }

    @Override
    public Vector3f getPosition() {
        return model.getCenterOfMass();
    }

    @Override
    public String toString() {
        return "STATIC." + getName();
    }
}
