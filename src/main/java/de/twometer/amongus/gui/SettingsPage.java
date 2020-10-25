package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.gui.Page;

public class SettingsPage extends Page {

    public SettingsPage() {
        super("Settings.html");
    }

    public void back() {
        AmongUs.get().getGuiManager().showPage(new MainMenuPage());
    }

}
