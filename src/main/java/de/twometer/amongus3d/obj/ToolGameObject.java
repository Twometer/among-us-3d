package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.model.ToolType;

public class ToolGameObject extends StaticGameObject {

    private final Room room;

    private final ToolType toolType;

    public ToolGameObject(String name, Renderable model, Room room, ToolType toolType) {
        super(name, model);
        this.room = room;
        this.toolType = toolType;
    }

    @Override
    public String toString() {
        return "TOOL." + room + "." + toolType;
    }

    @Override
    public boolean canPlayerInteract() {
        return true;
    }
}
