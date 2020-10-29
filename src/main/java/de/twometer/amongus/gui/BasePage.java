package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.neko.gui.Page;

public abstract class BasePage extends Page {

    protected final AmongUs amongUs;

    private final Page previous;

    public BasePage(String path) {
        super(path);
        amongUs = AmongUs.get();
        previous = amongUs.getGuiManager().getCurrentPage();
    }

    protected final void goBack() {
        AmongUs.get().getGuiManager().showPage(previous);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        ApiGui.reset();
    }
}
