package de.twometer.amongus3d.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.twometer.amongus3d.audio.SoundBuffer;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.server.ServerMain;
import de.twometer.amongus3d.ui.screen.EjectScreen;
import de.twometer.amongus3d.ui.screen.EmergencyScreen;
import de.twometer.amongus3d.ui.screen.GameEndScreen;
import de.twometer.amongus3d.ui.screen.GameStartScreen;
import de.twometer.amongus3d.util.Constants;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AmongUsClient {

    private Client client;

    private final List<CallbackItem> callbackItems = new CopyOnWriteArrayList<>();

    public Map<String, Player> users = new HashMap<>();

    public boolean gameEnded = false;
    public Role winner;

    public String gameCode;
    public boolean isHost;

    public float taskProgress;

    private static class CallbackItem {
        private static int idc = 0;
        private final int id;
        private Class<?> clazz;
        private Consumer consumer;

        public CallbackItem() {
            id = idc++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CallbackItem that = (CallbackItem) o;
            return id == that.id &&
                    Objects.equals(clazz, that.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, clazz);
        }
    }

    private void handle(Object o) {

        if (o instanceof NetMessage.PlayerJoined) {
            String usr = ((NetMessage.PlayerJoined) o).username;
            users.put(usr, new Player(usr));
        } else if (o instanceof NetMessage.EmergencyReport) {
            Log.i("Emergency meeting");
            Game.instance().getGameState().setCurrentState(GameState.State.Emergency);
            Game.instance().getGuiRenderer().setCurrentScreen(new EmergencyScreen(((NetMessage.EmergencyReport) o).deathReport, ((NetMessage.EmergencyReport) o).reporter, ((NetMessage.EmergencyReport) o).voteDuration));
        } else if (o instanceof NetMessage.GameStarted) {
            NetMessage.GameStarted started = (NetMessage.GameStarted) o;
            Log.i("Game starting " + o.toString());

            Player player = Game.instance().getSelf();
            Game.instance().getCamera().getPosition().x = started.position.x;
            Game.instance().getCamera().getPosition().z = started.position.z;
            player.setPosition(Game.instance().getCamera().getPosition());
            player.setRole(started.role);
            player.setColor(started.color);
            player.setTasks(started.tasks);
            Collections.shuffle(player.getTasks());

            for (String impostor : ((NetMessage.GameStarted) o).impostors) {
                getPlayer(impostor).setRole(Role.Impostor);
            }

            Game.instance().getGuiRenderer().setCurrentScreen(new GameStartScreen());
        } else if (o instanceof NetMessage.PlayerEjected) {
            NetMessage.PlayerEjected ejected = (NetMessage.PlayerEjected) o;
            int remainingImpostors = getRemainingImpostors();
            if (ejected.username.equals(Constants.INVALID_USER)) {
                Game.instance().getGuiRenderer().setCurrentScreen(new EjectScreen("No one was ejected (Tie)", remainingImpostors + " impostors remain"));
                return;
            } else if (ejected.username.equals(Constants.SKIP_USER)) {
                Game.instance().getGuiRenderer().setCurrentScreen(new EjectScreen("No one was ejected (Skipped)", remainingImpostors + " impostors remain"));
                return;
            }

            if (ejected.confirm) {
                String ejectMsg = ejected.impostor ? " was the impostor" : " was not an impostor";
                Game.instance().getGuiRenderer().setCurrentScreen(new EjectScreen(ejected.username + ejectMsg, remainingImpostors + " impostors remain"));
            } else {
                Game.instance().getGuiRenderer().setCurrentScreen(new EjectScreen(ejected.username + " was ejected", ""));
            }
        } else if (o instanceof NetMessage.PlayerKill) {
            //if (Game.instance().getGameState().getCurrentState() == GameState.State.Running) {
            //SoundBuffer buffer = Game.instance().getSoundProvider().getBuffer("kill.ogg")
            //}
            getPlayer(((NetMessage.PlayerKill) o).victim).setDead(true);
            if (((NetMessage.PlayerKill) o).victim.equals(Game.instance().getSelf().getUsername()))
                Game.instance().getSelf().setDead(true);
        } else if (o instanceof NetMessage.GameEnded) {
            gameEnded = true;
            winner = ((NetMessage.GameEnded) o).winner;
            Log.i("Game end, win: " + winner);

            if (Game.instance().getGameState().getCurrentState() == GameState.State.Running) {
                Game.instance().getGuiRenderer().setCurrentScreen(new GameEndScreen());
            }
        } else if (o instanceof NetMessage.GameJoined) {
            isHost = ((NetMessage.GameJoined) o).host;

        }
    }


    private int getRemainingImpostors() {
        int impostors = 0;
        for (Player player : users.values())
            if (!player.isDead() && player.getRole() == Role.Impostor)
                impostors++;
        return impostors;
    }

    public void connect() {
        client = new Client();
        NetMessage.registerAll(client.getKryo());


        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                // Log.i("Incoming message " + o.getClass().getSimpleName());

                List<CallbackItem> remove = new ArrayList<>();
                for (CallbackItem itm : callbackItems) {
                    if (itm.clazz == o.getClass()) {
                        itm.consumer.accept(o);
                        remove.add(itm);
                    }
                }
                //  Log.d("Forwarded to " + remove.size() + " callbacks");
                callbackItems.removeAll(remove);
                handle(o);
                EventBus.getDefault().post(o);
            }

            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                Log.i("Connected.");
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                Log.w("Connection lost");
            }
        });

        client.start();
        try {
            client.connect(5000, "127.0.0.1", ServerMain.PORT1, ServerMain.PORT2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private boolean connecting = false;

    public AmongUsClient sendMessage(Object message) {
        EXECUTOR.execute(() -> {
            if (client == null || (!client.isConnected() && !connecting)) {
                connecting = true;
                connect();
                connecting = false;
            }

            client.sendTCP(message);
        });
        return this;
    }

    public <T> void awaitReply(Class<T> packet, Consumer<T> consumer) {
        CallbackItem i = new CallbackItem();
        i.clazz = packet;
        i.consumer = consumer;
        callbackItems.add(i);
        // Log.d("Registered callback " + i.id + " on Net::" + i.clazz.getSimpleName());
    }

    public Player getPlayer(String player) {
        return users.get(player);
    }
}
