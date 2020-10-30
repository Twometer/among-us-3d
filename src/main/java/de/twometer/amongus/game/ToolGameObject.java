package de.twometer.amongus.game;

import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.ToolType;
import de.twometer.neko.render.model.ModelBase;

public class ToolGameObject extends GameObject {

    private final Location location;
    private final ToolType toolType;

    public ToolGameObject(ModelBase model, Location location, ToolType toolType) {
        super(model);
        this.location = location;
        this.toolType = toolType;
    }

    @Override
    public boolean canInteract() {
        return true;
    }

}
