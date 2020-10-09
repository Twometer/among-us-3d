package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.ToolType;
import de.twometer.amongus3d.util.Log;

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

    @Override
    public void onClicked() {
        super.onClicked();
        Log.d("Clicked on " + toString());
        if (toolType == ToolType.Emergency)
            Game.instance().getClient().sendMessage(new NetMessage.EmergencyReport());
    }
}
