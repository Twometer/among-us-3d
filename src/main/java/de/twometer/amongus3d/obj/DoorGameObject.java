package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.render.RenderLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DoorGameObject extends GameObject {

    private final Room room;

    private Renderable leftDoor;

    private Renderable rightDoor;

    private final int firstHalfId;

    private Vector3f closeVector;
    private Vector3f closeVectorInv;

    private boolean isClosed;

    public DoorGameObject(String name, Room room, Renderable firstHalf, int firstHalfId) {
        super(name);
        this.room = room;
        this.leftDoor = firstHalf;
        this.firstHalfId = firstHalfId;
    }

    public Room getRoom() {
        return room;
    }

    public void setSecondHalf(Renderable renderable) {
        rightDoor = renderable;
    }

    public int getFirstHalfId() {
        return firstHalfId;
    }

    @Override
    public void init() {
        Vector3f c0 = leftDoor.getCenterOfMass();
        Vector3f c1 = rightDoor.getCenterOfMass();

        if (c0.x == c1.x) {
            if (c0.z > c1.z) {
                flipDoors();
            }

            closeVector = new Vector3f(0, 0, 1f);
        } else if (c0.z == c1.z) {
            if (c0.x > c1.x) {
                flipDoors();
            }

            closeVector = new Vector3f(1f, 0, 0);
        }

        closeVectorInv = new Vector3f();
        closeVector.mul(-1, closeVectorInv);
    }

    private void flipDoors() {
        Renderable t = rightDoor;
        rightDoor = leftDoor;
        leftDoor = t;
    }

    @Override
    public void render(RenderLayer layer) {
        if (layer != RenderLayer.Base)
            return;

        if (leftDoor == null || rightDoor == null)
            throw new IllegalStateException("Incomplete door object");

        if (isClosed) {
            leftDoor.render(new Matrix4f().translate(closeVector));
            rightDoor.render(new Matrix4f().translate(closeVectorInv));
        } else {
            leftDoor.render();
            rightDoor.render();
        }
    }

    @Override
    public boolean canPlayerInteract() {
        return false;
    }

    @Override
    public String toString() {
        return "DOOR." + room;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
