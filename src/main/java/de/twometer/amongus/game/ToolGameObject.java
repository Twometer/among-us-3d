package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.gui.CallMeetingPage;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.Session;
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
        var myself = AmongUs.get().getSession().getMyself();
        return myself != null && myself.alive;
    }

    @Override
    public void onClick() {
        super.onClick();
        if (toolType == ToolType.Emergency) {
            AmongUs.get().getGuiManager().showPage(new CallMeetingPage());
        }
    }
}
