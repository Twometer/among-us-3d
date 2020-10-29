package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;

public class LobbyPage extends BasePage {

    public LobbyPage() {
        super("Lobby.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.setElementText("gamecode", AmongUs.get().getSession().getGameCode());
    }

    public void start() {

    }

    public void customize() {

    }

    public void disconnect() {

    }

}
