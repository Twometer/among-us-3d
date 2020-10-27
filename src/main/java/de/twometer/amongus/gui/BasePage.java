package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.amongus.util.RemoteGuiApi;
import de.twometer.neko.gui.Page;

public abstract class BasePage extends Page {

    private final Page previous;

    public BasePage(String path) {
        super(path);
        previous = AmongUs.get().getGuiManager().getCurrentPage();
    }

    protected final void goBack() {
        AmongUs.get().getGuiManager().showPage(previous);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        RemoteGuiApi.reset();
    }
}
