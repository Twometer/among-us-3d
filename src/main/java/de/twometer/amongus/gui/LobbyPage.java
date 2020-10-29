package de.twometer.amongus.gui;

public class LobbyPage extends BasePage {

    private String code;

    public LobbyPage(String code) {
        super("Lobby.html");
        this.code = code;
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.setElementText("gamecode", code);
    }

    public void start() {

    }

    public void customize() {

    }

    public void disconnect() {

    }

}
