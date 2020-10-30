package de.twometer.amongus.game;

import de.twometer.amongus.model.Location;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.util.Log;

public class DoorGameObject extends GameObject {

    private final ModelBase secondHalf;
    private final Location location;
    private final int index;

    public DoorGameObject(ModelBase firstHalf, ModelBase secondHalf, Location location, int index) {
        super(firstHalf);
        this.secondHalf = secondHalf;
        this.location = location;
        this.index = index;
    }

    @Override
    public boolean canInteract() {
        return false;
    }

}
