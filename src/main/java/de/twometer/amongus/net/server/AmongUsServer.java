package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.model.*;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.AsyncScheduler;
import de.twometer.amongus.util.Config;
import de.twometer.amongus.util.RandomUtil;
import de.twometer.neko.util.Log;
import de.twometer.neko.util.MathF;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
            tpAllToSpawn(p.session);

            // Assign tasks and start game
            var commonTask = TaskGenerator.newCommonTask();
            for (var player : p.session.getPlayers()) {
                var list = new ArrayList<PlayerTask>();
                list.add(commonTask);
                generateTasks(list, p.session.getConfig());
                Collections.shuffle(list);

                if (player.getRole() != PlayerRole.Impostor)
                    for (var task : list)
                        p.session.totalTaskStages += task.length();

                player.sendTCP(new NetMessage.OnGameStart(list));
            }
        });
        handlers.register(NetMessage.PositionChange.class, (p, m) -> {
            if (p.session == null) return;
            p.player.position = m.position;
            p.player.rotation = m.rotation;
            m.playerId = p.player.id;
            p.session.broadcastExcept(m, m.playerId);
        });
        handlers.register(NetMessage.CompleteTaskStage.class, (p, m) -> {
            if (p.session == null) return;
            p.session.tasksFinished++;
            p.session.broadcast(new NetMessage.OnTaskProgressChanged(p.session.getTaskProgress()));
            checkVictory(p.session);
        });
        handlers.register(NetMessage.CallEmergency.class, (p, m) -> {
            if (p.session == null) return;
            var broadcast = new NetMessage.OnEmergencyMeeting(p.player.id, m.cause);
            p.session.broadcast(broadcast);
            p.session.votingTask = scheduler.runLater(p.session.getConfig().getVotingTime() * 1000, () -> {
                p.session.votingTask = null;
                handleVotingEnd(p.session);
            });
        });
        handlers.register(NetMessage.Vote.class, (p, m) -> {
            if (p.session == null) return;
            if (p.session.votes.containsKey(p.player.id)) // no double votes
                return;

            var broadcast = new NetMessage.OnPlayerVoted(p.player.id, m.playerId);
            p.session.broadcast(broadcast);
            p.session.votes.put(p.player.id, m.playerId);
            checkAllVoted(p.session);
        });
        handlers.register(NetMessage.Kill.class, (p, m) -> {
            if (p.session == null) return;
            var victim = p.session.getPlayer(m.playerId);
            victim.player.alive = false;
            p.session.broadcast(new NetMessage.Kill(false, victim.player.id));
            p.session.broadcast(new NetMessage.PositionChange(p.player.id, victim.player.position, victim.player.rotation));
            checkVictory(p.session);
        });
        handlers.register(NetMessage.StartSabotage.class, (p, m) -> {
            if (p.session == null) return;
            p.session.broadcast(new NetMessage.OnSabotageStateChanged(m.sabotage, true, 30000, CodeGenerator.newO2Code()));
            if (m.sabotage == Sabotage.O2 || m.sabotage == Sabotage.Reactor) {
                p.session.sabotageTask = scheduler.runLater(30000, () -> {
                    p.session.broadcast(new NetMessage.OnGameEnd(PlayerRole.Impostor));
                });
            }
        });
        handlers.register(NetMessage.FixSabotage.class, (p, m) -> {
            if (p.session == null) return;
            if (m.fixed) p.session.fixers++;
            else p.session.fixers--;

            if (m.sabotage == Sabotage.Reactor || m.sabotage == Sabotage.O2) {
                if (p.session.fixers == 2) {
                    scheduler.cancel(p.session.sabotageTask);
                    p.session.broadcast(new NetMessage.OnSabotageStateChanged(m.sabotage, false, 0, ""));
                }
            } else if (m.sabotage == Sabotage.Comms || m.sabotage == Sabotage.Lights) {
                if (p.session.fixers > 0) {
                    p.session.broadcast(new NetMessage.OnSabotageStateChanged(m.sabotage, false, 0, ""));
                }
            }
        });
    }

    private void tpAllToSpawn(ServerSession session) {
        var center = new Vector3f(28.09f, 0.0f, -22.46f);
        var radius = 1.7f;
        var angleOffset = (2.0f * Math.PI) / session.getPlayers().size();
        var angle = 0f;
        for (var player : session.getPlayers()) {
            var position = new Vector3f(
                    center.x + MathF.sin(angle) * radius,
                    center.y,
                    center.z + MathF.cos(angle) * radius
            );
            session.broadcast(new NetMessage.PositionChange(player.getId(), position, 0));
            angle += angleOffset;
        }
    }

    private void handleVotingEnd(ServerSession session) {
        if (session.votingTask != null)
            scheduler.cancel(session.votingTask);
        session.broadcast(new NetMessage.OnVoteResults(session.votes));

        scheduler.runLater(8000, () -> {
            // Determine who was ejected

            Map<Integer, Integer> voteCounts = new HashMap<>();
            if (session.votes.isEmpty()) {
                session.broadcast(new NetMessage.OnPlayerEjected(NetMessage.OnPlayerEjected.Result.Tie, 0));
                return;
            }

            for (var vote : session.votes.entrySet()) {
                var dstPlayerId = vote.getValue();
                if (!voteCounts.containsKey(dstPlayerId))
                    voteCounts.put(dstPlayerId, 1);
                else
                    voteCounts.put(dstPlayerId, voteCounts.get(dstPlayerId) + 1);
            }
            var sorted = voteCounts.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .collect(Collectors.toList());

            var highestVotes = sorted.get(0);
            var secondHighest = sorted.size() > 1 ? sorted.get(1) : null;

            if (secondHighest != null && highestVotes.getValue().equals(secondHighest.getValue())) {
                // Tie
                session.broadcast(new NetMessage.OnPlayerEjected(NetMessage.OnPlayerEjected.Result.Tie, 0));
            } else if (highestVotes.getKey() == Player.SKIP_PLAYER) {
                // Skip
                session.broadcast(new NetMessage.OnPlayerEjected(NetMessage.OnPlayerEjected.Result.Skip, 0));
            } else {
                // Eject
                int ejectedPlayer = highestVotes.getKey();
                session.getPlayer(ejectedPlayer).player.alive = false;
                session.broadcast(new NetMessage.Kill(true, ejectedPlayer));
                session.broadcast(new NetMessage.OnPlayerEjected(NetMessage.OnPlayerEjected.Result.Eject, ejectedPlayer));
            }

            tpAllToSpawn(session);

            session.votes.clear();

            checkVictory(session);
        });
    }

    private void checkAllVoted(ServerSession session) {
        if (session.votes.size() == session.getPlayers().size())
            handleVotingEnd(session);
    }

    private void checkVictory(ServerSession session) {
        var winners = findWinners(session);
        if (winners == null) return;
        session.broadcast(new NetMessage.OnGameEnd(winners));
    }

    private PlayerRole findWinners(ServerSession session) {
        if (session.getTaskProgress() == 1.0f)
            return PlayerRole.Crewmate;

        var impostors = 0;
        var crewmates = 0;
        for (var player : session.getPlayers()) {
            if (!player.player.alive) continue;
            if (player.getRole() == PlayerRole.Impostor) impostors++;
            else crewmates++;
        }
        if (impostors == 0) return PlayerRole.Crewmate;
        else if (impostors >= crewmates) return PlayerRole.Impostor;
        else return null;
    }

    private void generateTasks(List<PlayerTask> list, SessionConfig config) {
        for (var i = 0; i < config.getShortTasks(); i++) {
            PlayerTask shortTask = generate(list, TaskGenerator::newShortTask);
            list.add(shortTask);
        }

        for (var i = 0; i < config.getLongTasks(); i++) {
            PlayerTask longTask = generate(list, TaskGenerator::newLongTask);
            list.add(longTask);
        }
    }

    private <T> T generate(List<T> list, Supplier<T> supplier) {
        T t;
        var tries = 0;
        do {
            t = supplier.get();
            tries++;
            if (tries > 25)
                throw new IllegalStateException("Exceeded random tries.");
        } while (list.contains(t));
        return t;
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
