package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.gui.FixSabotagePage;
import de.twometer.amongus.model.Sabotage;
import de.twometer.neko.render.Color;
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
    public void onClick() {
        super.onClick();
        AmongUs.get().getGuiManager().showPage(new FixSabotagePage(sabotage));
    }

    @Override
    public Color getHighlightColor() {
        return new Color(1, 0, 0, 0.75f);
    }

    @Override
    public boolean canInteract() {
        return AmongUs.get().getSession().currentSabotage == sabotage;
    }
}
