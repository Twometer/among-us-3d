package de.twometer.amongus.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Session<P extends PlayerBehavior> {

    private final String gameCode;

    private int host;

    protected final List<P> players = new ArrayList<>();

    private SessionConfig config;

    public Session(String gameCode, int host) {
        this.gameCode = gameCode;
        this.host = host;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setHost(int host) {
        this.host = host;
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

    public void removePlayer(int id) {
        players.removeIf(p -> p.getId() == id);
    }

    public P getPlayer(int id) {
        for (var player : players)
            if (player.getId() == id)
                return player;
        throw new IllegalArgumentException("Unknown player id " + id);
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
