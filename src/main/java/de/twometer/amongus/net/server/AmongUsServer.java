package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.model.PlayerRole;
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
    public void disconnected(Connection c) {
        var connection = ((PlayerConnection) c);
        if (connection.session != null) {
            connection.session.removePlayer(connection.player.id);
            connection.session.broadcast(new NetMessage.OnPlayerLeave(connection.player.id));

            if (connection.session.getHost() == connection.player.id) {
                Log.i("Host left, finding new host");
                connection.session.findNewHost();
                connection.session.broadcast(new NetMessage.OnHostChanged(connection.session.getHost()));
            }

            if (connection.session.getPlayers().size() == 0) {
                Log.i("Session is empty, deleting it.");
                sessions.remove(connection.session.getGameCode());
            }
        }
        var playerId = connection.getId();
        Log.i("#" + playerId + " disconnected. " + connectedClients.decrementAndGet() + " clients online.");
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
            else if (m.username == null || m.username.trim().length() == 0)
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.InvalidUsername));
            else if (session.isUsernameTaken(m.username))
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.UsernameTaken));
            else if (session.isFull())
                p.sendTCP(new NetMessage.SessionJoined(NetMessage.SessionJoined.Result.LobbyFull));
            else {
                // Configure the player object
                p.session = session;
                p.player.username = m.username;
                p.player.role = PlayerRole.Crewmate;
                p.player.color = p.session.getRandomFreeColor();

                // Send join success message
                p.sendTCP(new NetMessage.SessionJoined(p.player.id, session.getGameCode(), NetMessage.SessionJoined.Result.Success, p.session.getHost()));

                // Send list of currently online players
                for (var player : session.getPlayers())
                    p.sendTCP(new NetMessage.OnPlayerJoin(player.getId(), player.getUsername(), player.getColor()));

                // Send session config
                p.sendTCP(new NetMessage.OnSessionUpdate(session.getConfig()));

                // Broadcast that they joined
                p.session.addPlayer(p);
                p.session.broadcast(new NetMessage.OnPlayerJoin(p.player.id, p.player.username, p.player.color));

                // Init client side player object
                p.sendTCP(new NetMessage.OnPlayerUpdate(p.player.id, p.player.color, p.player.role));
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
            p.session.setConfig(m.config);
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
