package de.twometer.amongus.model;

public class ClientSession extends Session<Player> {

    private int myPlayerId;

    public ClientSession(String gameCode) {
        super(gameCode, -1);
    }

    public void setMyPlayerId(int myPlayerId) {
        this.myPlayerId = myPlayerId;
    }

    public Player getMyself() {
        for (Player player : getPlayers())
            if (player.id == myPlayerId)
                return player;
        throw new RuntimeException("Can't find myself.");
    }

}
