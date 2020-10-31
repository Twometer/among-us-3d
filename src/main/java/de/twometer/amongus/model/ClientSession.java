package de.twometer.amongus.model;

public class ClientSession extends Session<Player> {

    private int myPlayerId;

    public float taskProgress;

    public long lastEmergency = 0;

    public PlayerRole winners;

    public Sabotage currentSabotage = null;
    public String currentSabotageCode = "";
    public int currentSabotageDuration = 0;

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
