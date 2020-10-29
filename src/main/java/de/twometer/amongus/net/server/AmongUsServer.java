package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.AsyncScheduler;
import de.twometer.amongus.util.Config;
import de.twometer.neko.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AmongUsServer extends Listener {

    private final Server server = new Server() {
        @Override
        protected Connection newConnection() {
            return new PlayerConnection();
        }
    };

    private final Map<String, ServerSession> sessions = new ConcurrentHashMap<>();
    private final HandlerRegistry handlers = new HandlerRegistry();
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

        Log.i("Binding to *:" + config.getServerPort());
        server.addListener(this);
        server.bind(config.getServerPort());

        registerHandlers();
        Log.i("Server is online.");
    }

    @Override
    public void connected(Connection connection) {
        var playerId = newId();
        ((PlayerConnection) connection).playerId = playerId;
        Log.i(connection.getRemoteAddressTCP().toString() + " (#" + playerId + ") connected. " + connectedClients.incrementAndGet() + " clients online.");
    }

    @Override
    public void disconnected(Connection connection) {
        Log.i(connection.getRemoteAddressTCP().toString() + " disconnected. " + connectedClients.decrementAndGet() + " clients online.");
    }

    @Override
    public void received(Connection c, Object o) {
        var connection = (PlayerConnection) c;
        Log.d("Incoming message: " + o.toString());
        handlers.handle(connection, o);
    }

    private void registerHandlers() {
        handlers.register(NetMessage.SessionJoin.class, (c, p) -> {
            var session = getSession(p.code);
            if (session == null)
                c.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.InvalidGameCode));
            else if (p.username.trim().length() == 0)
                c.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.InvalidUsername));
            else if (session.isUsernameTaken(p.username))
                c.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.UsernameTaken));
            else if (session.isFull())
                c.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.LobbyFull));
            else {
                c.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.Success));
                session.join(c);
            }
        });
    }

    private int newId() {
        return idCounter.incrementAndGet();
    }

    private ServerSession getSession(String code) {
        return code == null || code.isEmpty() ? null : sessions.get(code);
    }

}
