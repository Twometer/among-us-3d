package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
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
        AmongUs.get().getUserSettings().setUsername(user.trim());
        AmongUs.get().getUserSettings().save();
    }

    public void createGame() {
        AmongUs.get().getGuiManager().showPage(new CustomizePage());
    }

    public void joinGame(String gameCode) {
        Log.i("Game code: " + gameCode);

        if (gameCode.trim().length() == 0)  {
            showError("Invalid game code!");
        } else {
            showLoading("Connecting...");
        }
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

    private void showLoading(String msg) {
        context.call("showDialog", "loading");
        context.setElementText("loadingMessage", msg);
    }

    private void hideLoading(String msg) {
        context.call("hideDialog", "loading");
    }

    private void showError(String msg) {
        context.call("showDialog", "error");
        context.setElementText("errorMessage",msg);
    }

}
