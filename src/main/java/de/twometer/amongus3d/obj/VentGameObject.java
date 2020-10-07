package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.IRenderable;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.model.Room;

public class VentGameObject extends StaticGameObject {

    private Room location;

    private int id;

    public VentGameObject(String name, IRenderable model, Room location, int id) {
        super(name, model);
        this.location = location;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("VENT.%s.%d", location, id);
    }



}
