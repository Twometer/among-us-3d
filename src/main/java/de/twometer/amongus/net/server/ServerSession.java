package de.twometer.amongus.net.server;

import de.twometer.amongus.model.PlayerColor;
import de.twometer.amongus.model.SessionConfig;

public class ServerSession {

    private String gameCode;

    private SessionConfig config;

    private final int host;

    public ServerSession(String gameCode, int host) {
        this.gameCode = gameCode;
        this.host = host;
    }

    public void configure(SessionConfig config) {
        this.config = config;
    }

    public void join(PlayerConnection c) {
        c.session = this;
    }

    public boolean isColorAvailable(PlayerColor color) {
        return true;
    }

    public void broadcast(Object message) {

    }

    public boolean isFull() {
        return false;
    }

    public boolean isUsernameTaken(String username) {
        return false;
    }

    public int getHost() {
        return host;
    }
}
