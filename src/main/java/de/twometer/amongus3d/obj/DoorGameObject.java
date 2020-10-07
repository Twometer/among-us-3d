package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.IRenderable;

public class DoorGameObject extends GameObject {

    private final GameRoom room;

    private final IRenderable leftDoor;

    private final IRenderable rightDoor;

    public DoorGameObject(String name, GameRoom room, IRenderable leftDoor, IRenderable rightDoor) {
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
}
