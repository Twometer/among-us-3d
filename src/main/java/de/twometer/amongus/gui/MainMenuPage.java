package de.twometer.amongus.gui;

import de.twometer.amongus.game.AmongUs;

public class MainMenuPage extends BasePage {

    public MainMenuPage() {
        super("MainMenu.html");
    }

    public void createGame() {
        AmongUs.get().getGuiManager().showPage(new CustomizePage());
    }

    public void joinGame() {
        AmongUs.get().getGuiManager().showPage(new IngamePage());
    }

    public void settings() {
        AmongUs.get().getGuiManager().showPage(new SettingsPage());
    }

    public void quit() {
        AmongUs.get().getWindow().close();
    }

    public void credits() {
        AmongUs.get().getGuiManager().showPage(new CreditsPage());
    }

}
