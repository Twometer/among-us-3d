package de.twometer.amongus3d.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {

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
                        session.sendToAll(new NetMessage.GameStarted());
                    }
                } else if (o instanceof NetMessage.EmergencyReport) {
                    ServerSession serverSession = getSession(connection);
                    if (serverSession !=null) {
                        NetMessage.EmergencyReport msg = new NetMessage.EmergencyReport();
                        msg.reporter = getPlayer(connection).player.getUsername();
                        msg.deathReport = ((NetMessage.EmergencyReport) o).deathReport;
                        serverSession.sendToAll(msg);
                    }
                }
            }
        });

        /*
        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                if (o instanceof CreateGameMessage) {
                    Log.i("Creating game");
                    Session session = new Session(SessionCodeGenerator.generate());
                    sessions.put(session.gameCode, session);
                    connection.sendTCP(new CreateGameMessageReply(session.gameCode));
                } else if (o instanceof JoinGameMessage) {
                    Log.i("Joining game");
                    if (sessions.containsKey(((JoinGameMessage) o).gameId)) {
                        Session session = sessions.get(((JoinGameMessage) o).gameId);
                        if (session.players.size() < 10 && session.gameState == GameState.State.Lobby) {
                            ServerPlayer player = new ServerPlayer(connection);
                            player.player = new Player(((JoinGameMessage) o).username, new Vector3f(0, 0, 0), Role.Crewmate);
                            session.players.add(player);
                            connection.sendTCP(new JoinGameMessageReply(true));
                            for (ServerPlayer otherPlayer : session.players) {
                                PlayerJoinedMessage joinedMessage = new PlayerJoinedMessage();
                                joinedMessage.username = ((JoinGameMessage) o).username;
                                otherPlayer.connection.sendTCP(joinedMessage);
                            }
                        } else {
                            connection.sendTCP(new JoinGameMessageReply(false));
                        }
                    } else {
                        connection.sendTCP(new JoinGameMessageReply(false));
                    }

                } else if (o instanceof StartGameMessage) {
                    for (ServerPlayer player : sessions.get(((StartGameMessage) o).gameCode).players)
                        player.connection.sendTCP(o);
                    sessions.get(((StartGameMessage) o).gameCode).gameState = GameState.State.Running;
                }
                System.out.println("receive message " + o.getClass().toString());
            }
        });*/
        Log.i("Server online");
    }

}

