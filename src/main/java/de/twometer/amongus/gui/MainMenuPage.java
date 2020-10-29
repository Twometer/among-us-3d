package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;

public class MainMenuPage extends BasePage {

    public MainMenuPage() {
        super("MainMenu.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.setElementProperty("username", "value", AmongUs.get().getUserSettings().getUsername());
    }

    public void updateUsername(String user) {
        AmongUs.get().getUserSettings().setUsername(user.trim());
        AmongUs.get().getUserSettings().save();
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
