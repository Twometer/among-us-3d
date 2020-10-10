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
import de.twometer.amongus3d.util.Log;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {

    public static final int COMMON_TASKS = 1;
    public static final int SHORT_TASKS = 2;
    public static final int LONG_TASKS = 2;

    public static final int PORT1 = 37832;
    public static final int PORT2 = 37833;

    private static final Map<String, ServerSession> sessions = new ConcurrentHashMap<>();

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
                        generateSession(session);

                        for (ServerPlayer player : session.players.values()) {
                            NetMessage.GameStarted started = new NetMessage.GameStarted();
                            started.position = player.player.getPosition();
                            started.role = player.player.getRole();
                            started.color = player.player.getColor();
                            started.tasks = player.player.getTasks();
                            player.connection.sendTCP(started);
                        }

                    }
                } else if (o instanceof NetMessage.EmergencyReport) {
                    ServerSession serverSession = getSession(connection);
                    if (serverSession != null) {
                        NetMessage.EmergencyReport msg = new NetMessage.EmergencyReport();
                        msg.reporter = getPlayer(connection).player.getUsername();
                        msg.deathReport = ((NetMessage.EmergencyReport) o).deathReport;
                        serverSession.sendToAll(msg);
                    }
                }
            }
        });
        Log.i("Server online");
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
                .addTask(new TaskDef(Room.values()[(int) (Math.random() * Room.values().length)], TaskType.values()[(int) (Math.random() * TaskType.values().length)]));
    }

    private static void createLongTask(Player player) {
        int r = (int) (Math.random() * 5);
        switch (r) {
            case 0:
                player.getTasks().add(new PlayerTask()
                        .addTask(new TaskDef(Room.Storage, TaskType.RefuelEngine))
                        .addTask(new TaskDef(Room.LowerEngine, TaskType.EngineMgmt))
                        .addTask(new TaskDef(Room.Storage, TaskType.RefuelEngine))
                        .addTask(new TaskDef(Room.UpperEngine, TaskType.EngineMgmt)));
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
            imposter = ((ServerPlayer)(serverSession.players.values().toArray()[(int) (Math.random() * serverSession.players.size())])).player.getUsername();
        } while (imposters.contains(imposter));
        return imposter;
    }

    private static void generateSession(ServerSession session) {
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
    }

}

