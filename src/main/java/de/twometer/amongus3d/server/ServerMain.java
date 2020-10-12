package de.twometer.amongus3d.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.PlayerColor;
import de.twometer.amongus3d.model.player.PlayerTask;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskDef;
import de.twometer.amongus3d.model.world.TaskType;
import de.twometer.amongus3d.util.Constants;
import de.twometer.amongus3d.util.Log;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ServerMain {

    public static final int COMMON_TASKS = 1;
    public static final int SHORT_TASKS = 2;
    public static final int LONG_TASKS = 2;
    public static final int VOTE_DURATION_SEC = 20;
    public static final int VOTE_DURATION_MS = 1000 * VOTE_DURATION_SEC;
    public static final boolean CONFIRM = true;

    public static final int PORT1 = 37832;
    public static final int PORT2 = 37833;

    private static final Map<String, ServerSession> sessions = new ConcurrentHashMap<>();

    private static final Scheduler SCHEDULER = new Scheduler();


    private static ServerPlayer getPlayer(Connection connection) {
        String name = connection.toString();
        String sessionId = name.substring(0, name.indexOf('|'));
        String username = name.substring(sessionId.length() + 1);
        return sessions.get(sessionId).players.get(username);
    }

    private static ServerSession getSession(Connection connection) {
        String name = connection.toString();
        String sessionId = name.substring(0, name.indexOf('|'));
        return sessions.get(sessionId);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();


        NetMessage.registerAll(server.getKryo());

        server.bind(PORT1, PORT2);
        SCHEDULER.init();
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                Log.i("New connection");
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                Log.i("Connection lost");
            }

            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                Log.d("Incoming message: " + o.toString());
                if (o instanceof NetMessage.CreateGame) {
                    String gameId = SessionCodeGenerator.generate();
                    ServerSession session = new ServerSession(gameId);
                    sessions.put(gameId, session);
                    connection.sendTCP(new NetMessage.GameCreated(gameId));
                } else if (o instanceof NetMessage.JoinGame) {
                    String username = ((NetMessage.JoinGame) o).username;
                    String gameId = ((NetMessage.JoinGame) o).gameId;
                    ServerSession session = sessions.get(gameId);
                    if (session == null || session.players.containsKey(username) || session.gameState != GameState.State.Lobby)
                        connection.sendTCP(new NetMessage.GameJoined(false, false));
                    else {
                        if (session.players.size() == 0) {
                            session.host = username;
                            connection.sendTCP(new NetMessage.GameJoined(true, true));
                        } else {
                            connection.sendTCP(new NetMessage.GameJoined(true, false));
                        }
                        connection.setName(gameId + "|" + username);
                        session.addPlayer(connection, username);
                        session.sendToAll(new NetMessage.PlayerJoined(username));
                    }
                } else if (o instanceof NetMessage.StartGame) {
                    ServerSession session = getSession(connection);
                    if (session == null) {
                        Log.w("Starting null session: cancelled");
                        return;
                    }
                    if (session.gameState == GameState.State.Lobby /*&& session.players.size() > 4*/) {
                        List<String> impostors = generateSession(session);

                        for (ServerPlayer player : session.players.values()) {
                            NetMessage.GameStarted started = new NetMessage.GameStarted();
                            started.position = player.player.getPosition();
                            started.role = player.player.getRole();
                            started.color = player.player.getColor();
                            started.tasks = player.player.getTasks();
                            started.impostors = impostors;
                            player.connection.sendTCP(started);
                        }

                    }
                } else if (o instanceof NetMessage.EmergencyReport) {
                    ServerSession serverSession = getSession(connection);
                    if (serverSession != null) {
                        NetMessage.EmergencyReport msg = new NetMessage.EmergencyReport();
                        msg.reporter = getPlayer(connection).player.getUsername();
                        msg.deathReport = ((NetMessage.EmergencyReport) o).deathReport;
                        msg.voteDuration = VOTE_DURATION_MS;
                        serverSession.sendToAll(msg);
                        for (ServerPlayer player : serverSession.players.values())
                            player.player.resetVotes();
                        serverSession.skipVotes = 0;

                        Log.i("Voting ends in " + VOTE_DURATION_MS);
                        serverSession.autoSkipTask = SCHEDULER.runLater(VOTE_DURATION_MS, () -> {
                            Log.i("Voting ended automatically...");
                            closeVoting(serverSession);
                        });
                    }
                } else if (o instanceof NetMessage.VoteCast) {
                    ServerSession serverSession = getSession(connection);
                    if (serverSession != null) {
                        NetMessage.VoteCast msg = new NetMessage.VoteCast();
                        msg.srcUsername = getPlayer(connection).player.getUsername();
                        msg.dstUsername = ((NetMessage.VoteCast) o).dstUsername;
                        serverSession.sendToAll(msg);

                        if (msg.dstUsername.equals(Constants.SKIP_USER)) {
                            serverSession.skipVotes++;
                        } else {
                            serverSession.players.get(msg.dstUsername).player.vote();
                        }

                        int votes = 0;
                        for (ServerPlayer player : serverSession.players.values())
                            votes += player.player.getEjectionVotes();
                        votes += serverSession.skipVotes;
                        if (votes == serverSession.players.size()) {
                            closeVoting(serverSession);
                            Log.i("everyone has voted");
                        } else {
                            Log.i("votes: " + votes + " / " + serverSession.players.size());
                        }

                    }
                }
            }
        });
        Log.i("Server online");
    }

    private static void closeVoting(ServerSession serverSession) {
        serverSession.sendToAll(new NetMessage.VotingEnd());
        SCHEDULER.cancel(serverSession.autoSkipTask);

        Log.i("Voting closed, autoSkip cancelled  " + serverSession.autoSkipTask);
        SCHEDULER.runLater(5000, () -> {
            Log.i("Eval voting results...");
            List<ServerPlayer> orderedByVotes = serverSession.players.values().stream().sorted(Comparator.comparingDouble(c -> c.player.getEjectionVotes())).collect(Collectors.toList());
            ServerPlayer mostVotes = orderedByVotes.get(0);
            int secondMostVotes = orderedByVotes.size() > 1 ? orderedByVotes.get(1).player.getEjectionVotes() : 0;

            Log.i("Most votes: " + mostVotes.player.getUsername() + " (" + mostVotes.player.getEjectionVotes() + ")");
            Log.i("Second most:" + secondMostVotes);
            Log.i("Skip votes: " + serverSession.skipVotes);

            if (serverSession.skipVotes == mostVotes.player.getEjectionVotes() || (mostVotes.player.getEjectionVotes() == secondMostVotes && mostVotes.player.getEjectionVotes() != 0)) {
                // Tie
                NetMessage.PlayerEjected ejected = new NetMessage.PlayerEjected();
                ejected.username = Constants.INVALID_USER;
                ejected.confirm = CONFIRM;
                ejected.impostor = false;
                serverSession.sendToAll(ejected);
            } else if (serverSession.skipVotes > mostVotes.player.getEjectionVotes()) {
                // Skip
                NetMessage.PlayerEjected ejected = new NetMessage.PlayerEjected();
                ejected.username = Constants.SKIP_USER;
                ejected.confirm = CONFIRM;
                ejected.impostor = false;
                serverSession.sendToAll(ejected);
            } else {
                // Eject
                NetMessage.PlayerKill kill = new NetMessage.PlayerKill();
                kill.attacker = Constants.INVALID_USER;
                kill.victim = mostVotes.player.getUsername();
                serverSession.sendToAll(kill);

                NetMessage.PlayerEjected ejected = new NetMessage.PlayerEjected();
                ejected.username = mostVotes.player.getUsername();
                ejected.confirm = CONFIRM;
                ejected.impostor = mostVotes.player.getRole() == Role.Impostor;
                serverSession.sendToAll(ejected);
            }
        });

    }

    private static Vector3f createPosition(int idx) {
        Vector3f base = new Vector3f(30, 0, -21);
        Vector3f add = new Vector3f((float) Math.sin(idx), 0, (float) Math.cos(idx));
        return base.add(add.normalize(4));
    }

    private static PlayerColor assignColor(int idx) {
        return PlayerColor.values()[idx];
    }

    private static PlayerTask randomBasicTask() {
        return new PlayerTask()
                .addTask(TaskGenerator.genUnique(null, TaskGenerator.SHORT_TASKS));
    }

    private static final Random r = new Random();

    private static void createLongTask(Player player) {
        switch (r.nextInt(4)) {
            case 0:
                player.getTasks().add(new PlayerTask()
                        .addTask(new TaskDef(Room.Storage, TaskType.RefuelEngine))
                        .addTask(new TaskDef(Room.LowerEngine, TaskType.EngineMgmt))
                        .addTask(new TaskDef(Room.Storage, TaskType.RefuelEngine))
                        .addTask(new TaskDef(Room.UpperEngine, TaskType.EngineMgmt)));
                break;
            case 1:
                player.getTasks().add(new PlayerTask()
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.ENERGY_DIVERT))
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.ENERGY_ACCEPT)));
                break;
            case 2:
                player.getTasks().add(new PlayerTask()
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.DATA_DOWNLOAD))
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.DATA_UPLOAD)));
                break;
            case 3:
                player.getTasks().add(new PlayerTask()
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.WIRING))
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.WIRING))
                        .addTask(TaskGenerator.genUnique(player, TaskGenerator.WIRING)));
                break;
        }
    }

    private static void createShortTask(Player player) {
        player.getTasks().add(randomBasicTask());
    }

    private static void createTasksFor(Player player) {

        for (int i = 0; i < LONG_TASKS; i++) {
            createLongTask(player);
        }

        for (int i = 0; i < SHORT_TASKS; i++) {
            createShortTask(player);
        }

    }

    private static String genImposter(List<String> imposters, ServerSession serverSession) {
        String imposter;
        do {
            imposter = ((ServerPlayer) (serverSession.players.values().toArray()[(int) (Math.random() * serverSession.players.size())])).player.getUsername();
        } while (imposters.contains(imposter));
        return imposter;
    }

    private static List<String> generateSession(ServerSession session) {
        PlayerTask com = randomBasicTask();
        int numImposters = session.players.size() > 7 ? 2 : 1;

        List<String> imposters = new ArrayList<>();
        for (int i = 0; i < numImposters; i++)
            imposters.add(genImposter(imposters, session));

        int playerIdx = 0;
        for (ServerPlayer player : session.players.values()) {
            player.player.setPosition(createPosition(playerIdx));
            player.player.setColor(assignColor(playerIdx));
            player.player.getTasks().add(com);
            createTasksFor(player.player);
            player.player.setRole(imposters.contains(player.player.getUsername()) ? Role.Impostor : Role.Crewmate);
            playerIdx++;
        }
        return imposters;
    }

}

