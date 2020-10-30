package de.twometer.amongus.game;

import de.twometer.amongus.model.Sabotage;
import de.twometer.neko.render.model.ModelBase;

public class SabotageGameObject extends GameObject {

    private final Sabotage sabotage;
    private final int index;

    public SabotageGameObject(ModelBase model, Sabotage sabotage, int index) {
        super(model);
        this.sabotage = sabotage;
        this.index = index;
    }

    @Override
    public boolean canInteract() {
        return false;
    }
}
