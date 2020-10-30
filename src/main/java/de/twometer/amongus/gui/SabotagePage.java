package de.twometer.amongus.gui;

import de.twometer.amongus.model.Sabotage;

public class SabotagePage extends BasePage {

    public SabotagePage(Sabotage sabotage) {
        super("Tasks/Fix" + sabotage.name() + ".html");
    }

    public void setFixing(boolean fixing) {
        // TODO Send fix packet
        // amongUs.getScheduler().runLater(800, this::goBack);
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }

}
