package de.twometer.amongus3d.net;

public class JoinGameMessage {

    public String gameId;

    public String username;

    public JoinGameMessage() {

    }

    public JoinGameMessage(String gameId, String username) {
        this.gameId = gameId;
        this.username = username;
    }
}
