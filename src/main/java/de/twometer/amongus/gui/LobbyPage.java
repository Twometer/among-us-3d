package de.twometer.amongus.gui;

import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.event.Events;
import org.greenrobot.eventbus.Subscribe;

public class LobbyPage extends BasePage {

    public LobbyPage() {
        super("Lobby.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.setElementText("gamecode", amongUs.getSession().getGameCode());

        for (var player : amongUs.getSession().getPlayers()) {
            addPlayer(player.id, player.username, player.color.name());
        }
        Events.register(this);

        if (amongUs.getSession().getMyself().getId() != amongUs.getSession().getHost())
            context.call("iAintHost");
    }

    public void start() {

    }

    public void customize() {

    }

    public void disconnect() {
        amongUs.getClient().disconnect();
        amongUs.getGuiManager().showPage(new MainMenuPage());
    }

    @Subscribe
    public void onJoin(NetMessage.OnPlayerJoin join) {
        addPlayer(join.id, join.username, join.color.name());
    }

    @Subscribe
    public void onLeave(NetMessage.OnPlayerLeave leave) {
        context.call("removePlayer", leave.id);
    }

    private void addPlayer(int id, String username, String color) {
        context.call("addPlayer", id, username, color);
    }

}
