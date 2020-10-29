package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import de.twometer.amongus.model.Player;

public class PlayerConnection extends Connection {

    public Player player = new Player();

    public ServerSession session;

}
