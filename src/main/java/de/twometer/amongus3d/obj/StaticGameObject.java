package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;

public class StaticGameObject extends GameObject {

    private final Renderable model;

    public StaticGameObject(String name, Renderable model) {
        super(name);
        this.model = model;
    }

    @Override
    public void render() {
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
