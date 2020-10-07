package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.render.RenderLayer;

public class StaticGameObject extends GameObject {

    final Renderable model;

    public StaticGameObject(String name, Renderable model) {
        super(name);
        this.model = model;
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
    public String toString() {
        return "STATIC." + getName();
    }
}
