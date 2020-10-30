package de.twometer.amongus.game;

import de.twometer.neko.render.model.ModelBase;

public class StaticGameObject extends GameObject {

    public StaticGameObject(ModelBase model) {
        super(model);
    }

    @Override
    public boolean canInteract() {
        return false;
    }

}
