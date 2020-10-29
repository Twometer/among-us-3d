package de.twometer.amongus.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Session<P extends PlayerBehavior> {

    private final String gameCode;

    private final int host;

    protected final List<P> players = new ArrayList<>();

    private SessionConfig config;

    public Session(String gameCode, int host) {
        this.gameCode = gameCode;
        this.host = host;
    }

    public String getGameCode() {
        return gameCode;
    }

    public int getHost() {
        return host;
    }

    public SessionConfig getConfig() {
        return config;
    }

    public void setConfig(SessionConfig config) {
        this.config = config;
    }

    public List<P> getPlayers() {
        return players;
    }

    public void addPlayer(P player) {
        players.add(player);
    }

    public boolean isColorAvailable(PlayerColor color) {
        for (var player : players)
            if (player.getColor() == color)
                return false;
        return true;
    }

    public boolean isFull() {
        return players.size() == PlayerColor.values().length;
    }

    public boolean isUsernameTaken(String username) {
        for (var player : players)
            if (player.getUsername().equalsIgnoreCase(username))
                return true;
        return false;
    }

}
