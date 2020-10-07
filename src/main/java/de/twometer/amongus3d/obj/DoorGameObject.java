package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.IRenderable;
import de.twometer.amongus3d.model.Room;

public class DoorGameObject extends GameObject {

    private final Room room;

    private final IRenderable leftDoor;

    private final IRenderable rightDoor;

    public DoorGameObject(String name, Room room, int id, IRenderable leftDoor, IRenderable rightDoor) {
        super(name);
        this.room = room;
        this.leftDoor = leftDoor;
        this.rightDoor = rightDoor;
    }

    @Override
    public void render() {
        leftDoor.render();
        rightDoor.render();
    }

    @Override
    public boolean canPlayerInteract() {
        return false;
    }

    @Override
    public String toString() {
        return "DOOR." + room;
    }
}
