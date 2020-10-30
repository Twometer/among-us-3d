package de.twometer.amongus.model;

public class ClientSession extends Session<Player> {

    private int myPlayerId;

    public float taskProgress;

    public ClientSession(String gameCode, int host) {
        super(gameCode, host);
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

    public int getMyPlayerId() {
        return myPlayerId;
    }

}
