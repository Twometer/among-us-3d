package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.AsyncScheduler;
import de.twometer.amongus.util.Config;
import de.twometer.amongus.util.RandomUtil;
import de.twometer.neko.util.Log;
import de.twometer.neko.util.MathF;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
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
    private final Random random = new Random();

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
        //Log.d("Incoming message: " + o.toString());
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
                broadcastPlayerUpdate(p);
            } else
                p.sendTCP(new NetMessage.ColorChanged(false));
        });
        handlers.register(NetMessage.StartGame.class, (p, m) -> {
            if (p.session == null) return;
            if (p.session.getHost() != p.player.id) return;

            // Choose impostors
            var playerCount = p.session.getPlayers().size();
            int preferredImpostors = p.session.getConfig().getImpostorCount();
            int maxImpostors = 3;
            if (playerCount < 6) maxImpostors = 1;
            else if (playerCount < 9) maxImpostors = 2;

            var impostors = Math.min(preferredImpostors, maxImpostors);
            for (var i = 0; i < impostors; i++) {
                var imp = randomImpostor(p.session);
                imp.player.role = PlayerRole.Impostor;
                broadcastPlayerUpdate(imp);
            }

            // Assign spawn locations
            var center = new Vector3f(28.09f, 0.0f, -22.46f);
            var radius = 1.7f;
            var angleOffset = (2.0f * Math.PI) / playerCount;
            var angle = 0f;
            for (var player : p.session.getPlayers()) {
                var position = new Vector3f(
                        center.x + MathF.sin(angle) * radius,
                        center.y,
                        center.z + MathF.cos(angle) * radius
                );
                p.session.broadcast(new NetMessage.PositionChange(player.getId(), position, 0));
                angle += angleOffset;
            }

            // Assign tasks and start game
            var commonTask = TaskGenerator.newCommonTask();
            for (var player : p.session.getPlayers()) {
                var list = new ArrayList<PlayerTask>();
                list.add(commonTask);

                for (var i = 0; i < p.session.getConfig().getShortTasks(); i++)
                    list.add(TaskGenerator.newShortTask());

                for (var i = 0; i < p.session.getConfig().getLongTasks(); i++)
                    list.add(TaskGenerator.newLongTask());

                Collections.shuffle(list);

                player.sendTCP(new NetMessage.OnGameStart(list));
            }
        });
        handlers.register(NetMessage.PositionChange.class, (p, m) -> {
            if (p.session == null) return;
            m.playerId = p.player.id;
            p.session.broadcastExcept(m, m.playerId);
        });
    }

    private void broadcastPlayerUpdate(PlayerConnection p) {
        p.session.broadcast(new NetMessage.OnPlayerUpdate(
                p.player.id,
                p.player.color,
                p.player.role
        ));
    }

    private PlayerConnection randomImpostor(ServerSession session) {
        PlayerConnection player;
        do {
            player = RandomUtil.getRandomItem(session.getPlayers());
        } while (player.player.role == PlayerRole.Impostor); // Don't select impostors twice
        return player;
    }

    private int newId() {
        return idCounter.incrementAndGet();
    }

    private ServerSession getSession(String code) {
        return code == null || code.isEmpty() ? null : sessions.get(code);
    }

}
