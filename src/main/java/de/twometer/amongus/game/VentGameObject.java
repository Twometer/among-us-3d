package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.neko.render.Color;
import de.twometer.neko.render.model.ModelBase;

public class VentGameObject extends GameObject {

    private final Location location;
    private final int index;

    public VentGameObject(ModelBase model, Location location, int index) {
        super(model);
        this.location = location;
        this.index = index;
    }

    @Override
    public boolean canInteract() {
        return AmongUs.get().getSession().getMyself().role == PlayerRole.Impostor;
    }

    @Override
    public Color getHighlightColor() {
        return new Color(1, 0, 0, 0.75f);
    }
}
