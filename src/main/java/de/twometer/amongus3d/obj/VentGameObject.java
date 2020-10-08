package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Room;

public class VentGameObject extends StaticGameObject {

    private final Room location;

    private final int ventIdx;

    public VentGameObject(String name, Renderable model, Room location, int ventIdx) {
        super(name, model);
        this.location = location;
        this.ventIdx = ventIdx;
    }

    @Override
    public String toString() {
        return String.format("VENT.%s.%d", location, ventIdx);
    }

    @Override
    public boolean canPlayerInteract() {
        return true;
    }
}
