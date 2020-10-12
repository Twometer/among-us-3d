package de.twometer.amongus3d.server;

import com.esotericsoftware.kryonet.Connection;
import de.twometer.amongus3d.model.player.Player;

public class ServerPlayer {

    public Connection connection;

    public Player player;

    public ServerSession session;

    public ServerPlayer(Connection connection, ServerSession session) {
        this.connection = connection;
        this.session = session;
    }
}
