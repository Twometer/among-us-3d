package de.twometer.amongus.net.server;

import de.twometer.amongus.model.PlayerColor;
import de.twometer.amongus.model.Session;
import de.twometer.amongus.util.RandomUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class ServerSession extends Session<PlayerConnection> {

    public int tasksFinished;
    public int totalTasks;

    public ServerSession(String gameCode, int host) {
        super(gameCode, host);
    }

    public float getTaskProgress() {
        if (totalTasks == 0) return 0;
        else return tasksFinished / (float) totalTasks;
    }

    public void findNewHost() {
        if (getPlayers().size() > 0) {
            var newHost = RandomUtil.getRandomItem(getPlayers());
            setHost(newHost.player.id);
        }
    }

    public PlayerColor getRandomFreeColor() {
        var colors = Arrays.stream(PlayerColor.values()).collect(Collectors.toList());
        for (var p : players)
            colors.remove(p.player.color);

        if (colors.size() == 0)
            throw new RuntimeException("Session is full");

        Collections.shuffle(colors);
        return colors.get(0);
    }

    public void broadcast(Object message) {
        for (var player : players)
            player.sendTCP(message);
    }

    public void broadcastExcept(Object message, int except) {
        for (var player : players)
            if (player.player.id != except)
                player.sendTCP(message);
    }

}
