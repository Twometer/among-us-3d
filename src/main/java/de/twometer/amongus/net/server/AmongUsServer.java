package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.AsyncScheduler;
import de.twometer.amongus.util.Config;
import de.twometer.neko.util.Log;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AmongUsServer extends Listener {

    private final Server server = new Server() {
        @Override
        protected Connection newConnection() {
            return new PlayerConnection();
        }
    };

    private final AsyncScheduler scheduler = new AsyncScheduler();
    private final AtomicInteger connectedClients = new AtomicInteger();
    private final AtomicInteger idCounter = new AtomicInteger();

    public AmongUsServer() {
        NetMessage.registerAll(server.getKryo());
        scheduler.start();
    }

    public void launch() throws IOException {
        Log.i("Starting Among Us 3D server...");

        var config = Config.get();

        server.start();
        server.bind(config.getServerPort());
        Log.i("Binding to *:" + config.getServerPort());
    }

    @Override
    public void connected(Connection connection) {
        var connected = connectedClients.incrementAndGet();
        var playerId = newId();
        ((PlayerConnection) connection).playerId = playerId;
        Log.i(connection.getEndPoint().toString() + " (#" + playerId + ") connected. " + connected + " clients online.");
    }

    @Override
    public void disconnected(Connection connection) {
        var connected = connectedClients.decrementAndGet();
        Log.i(connection.getEndPoint().toString() + " disconnected. " + connected + " clients online.");
    }

    @Override
    public void received(Connection c, Object o) {
        var connection = (PlayerConnection) c;

    }

    private int newId() {
        return idCounter.incrementAndGet();
    }

}
