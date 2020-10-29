package de.twometer.amongus.net.server;

import de.twometer.amongus.model.PlayerColor;
import de.twometer.amongus.model.Session;

public class ServerSession extends Session<PlayerConnection> {

    public ServerSession(String gameCode, int host) {
        super(gameCode, host);
    }

    public PlayerColor getRandomFreeColor() {
        return PlayerColor.Red;
    }

    public void broadcast(Object message) {
        for (var player : players)
            player.sendTCP(message);
    }

    public void handleJoin(PlayerConnection c) {
        c.session = this;
    }

    @Override
    public void addPlayer(PlayerConnection player) {
        super.addPlayer(player);
        player.session = this;
    }

}
