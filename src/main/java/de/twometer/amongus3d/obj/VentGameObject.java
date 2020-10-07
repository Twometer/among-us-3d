package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Room;

public class VentGameObject extends StaticGameObject {

    private final Room location;

    private final int id;

    public VentGameObject(String name, Renderable model, Room location, int id) {
        super(name, model);
        this.location = location;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("VENT.%s.%d", location, id);
    }



}
