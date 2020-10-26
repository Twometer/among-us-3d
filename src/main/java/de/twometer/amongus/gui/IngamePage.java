package de.twometer.amongus.gui;

import de.twometer.neko.gui.Page;

public class IngamePage extends BasePage {

    public IngamePage() {
        super("Ingame.html");
    }

    @Override
    public boolean isCursorVisible() {
        return false;
    }
}
