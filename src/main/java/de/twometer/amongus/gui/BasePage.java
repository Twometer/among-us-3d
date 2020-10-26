package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.gui.Page;

public abstract class BasePage extends Page {

    private final Page previous;

    public BasePage(String path) {
        super(path);
        previous = AmongUs.get().getGuiManager().getCurrentPage();
    }

    protected void goBack() {
        AmongUs.get().getGuiManager().showPage(previous);
    }

}
