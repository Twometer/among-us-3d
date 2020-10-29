package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.util.Log;

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
        amongUs.getUserSettings().setUsername(user.trim());
        amongUs.getUserSettings().save();
    }

    public void createGame() {
        amongUs.getGuiManager().showPage(new CustomizePage());
    }

    public void joinGame(String gameCode) {
        Log.i("Game code: " + gameCode);

        if (gameCode.trim().length() == 0) {
            showError("Invalid game code!");
        } else {
            showLoading("Joining...");
            sendJoinMessage(gameCode, () -> amongUs.getGuiManager().showPage(new LobbyPage(gameCode)));
        }
    }

    public void settings() {
        amongUs.getGuiManager().showPage(new SettingsPage());
    }

    public void quit() {
        amongUs.getWindow().close();
    }

    public void credits() {
        amongUs.getGuiManager().showPage(new CreditsPage());
    }

}
