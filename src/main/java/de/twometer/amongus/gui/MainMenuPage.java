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
            amongUs.getClient().sendMessage(new NetMessage.SessionJoin(amongUs.getUserSettings().getUsername(), gameCode))
                    .await(NetMessage.SessionJoined.class, r -> {
                        if (r.result == NetMessage.SessionJoined.Result.InvalidUsername)
                            showError("Invalid username!");
                        else if (r.result == NetMessage.SessionJoined.Result.LobbyFull)
                            showError("Sorry, this lobby is full!");
                        else if (r.result == NetMessage.SessionJoined.Result.UsernameTaken)
                            showError("Sorry, your username is already taken.");
                        else if (r.result == NetMessage.SessionJoined.Result.Other)
                            showError("An unknown error occurred joining a session.");
                        else
                            amongUs.getGuiManager().showPage(new LobbyPage());
                    })
                    .handleError(this::networkError);
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

    private void showLoading(String msg) {
        context.call("showDialog", "loading");
        context.setElementText("loadingMessage", msg);
    }

    private void hideLoading() {
        context.call("hideDialog", "loading");
    }

    private void networkError() {
        showError("Can't connect to the server");
    }

    private void showError(String msg) {
        hideLoading();
        context.call("showDialog", "error");
        context.setElementText("errorMessage", msg);
    }

}
