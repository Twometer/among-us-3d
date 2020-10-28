package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;

public class RoleRevealPage extends BasePage {
    public RoleRevealPage() {
        super("RoleReveal.html");
        AmongUs.get().getScheduler().runLater(4700, this::goBack);
    }
}
