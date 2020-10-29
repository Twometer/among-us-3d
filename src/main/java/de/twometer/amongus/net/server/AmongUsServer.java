package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.AsyncScheduler;
import de.twometer.amongus.util.Config;
import de.twometer.neko.util.Log;

import java.io.IOException;
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
        ((PlayerConnection) connection).player.id = playerId;
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
        handlers.register(NetMessage.SessionJoin.class, (p, m) -> {
            var session = getSession(m.code);
            if (session == null)
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.InvalidGameCode));
            else if (m.username.trim().length() == 0)
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.InvalidUsername));
            else if (session.isUsernameTaken(m.username))
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.UsernameTaken));
            else if (session.isFull())
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.LobbyFull));
            else {
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.Success));
                session.join(p);
            }
        });
        handlers.register(NetMessage.SessionCreate.class, (p, m) -> {
            var code = CodeGenerator.newGameCode();
            var session = new ServerSession(code, p.player.id);
            sessions.put(code, session);
            p.sendTCP(new NetMessage.SessionCreated(NetMessage.SessionCreated.Result.Ok, code));
        });
        handlers.register(NetMessage.SessionConfigure.class, (p, m) -> {
            if (p.session == null) return;
            if (p.session.getHost() != p.player.id) return;
            p.session.configure(m.config);
            p.sendTCP(new NetMessage.SessionConfigured(true));
        });
        handlers.register(NetMessage.ColorChange.class, (p, m) -> {
            if (p.session == null) return;
            if (p.session.isColorAvailable(m.newColor)) {
                p.player.color = m.newColor;
                p.sendTCP(new NetMessage.ColorChanged(true));
                p.session.broadcast(new NetMessage.OnPlayerUpdate(
                        p.player.id,
                        p.player.color,
                        p.player.role
                ));
            } else
                p.sendTCP(new NetMessage.ColorChanged(false));
        });
    }

    private int newId() {
        return idCounter.incrementAndGet();
    }

    private ServerSession getSession(String code) {
        return code == null || code.isEmpty() ? null : sessions.get(code);
    }

}
