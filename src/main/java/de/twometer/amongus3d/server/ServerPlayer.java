package de.twometer.amongus3d.server;

import com.esotericsoftware.kryonet.Connection;
import de.twometer.amongus3d.model.player.Player;

public class ServerPlayer {

    public Connection connection;

    public Player player;

    public ServerPlayer(Connection connection) {
        this.connection = connection;
    }
}
