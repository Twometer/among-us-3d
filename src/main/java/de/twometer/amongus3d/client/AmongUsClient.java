package de.twometer.amongus3d.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.server.ServerMain;
import de.twometer.amongus3d.ui.screen.EmergencyScreen;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class AmongUsClient {

    private Client client;

    private final List<CallbackItem> callbackItems = new CopyOnWriteArrayList<>();

    public List<String> users = new ArrayList<>();

    private static class CallbackItem {
        private Class<?> clazz;
        private Consumer consumer;
    }

    private void handle(Object o) {
        if (o instanceof NetMessage.PlayerJoined) {
            users.add(((NetMessage.PlayerJoined) o).username);
        } else if (o instanceof NetMessage.EmergencyReport) {
            Log.i("Emergency meeting");
            Game.instance().getGameState().setCurrentState(GameState.State.Emergency);
            Game.instance().getGuiRenderer().setCurrentScreen(new EmergencyScreen(((NetMessage.EmergencyReport) o).deathReport, ((NetMessage.EmergencyReport) o).reporter));
        } else if (o instanceof NetMessage.GameStarted) {
            Log.i("Game starting " + o.toString());
            SoundFX.play("game_start");
            Player player = Game.instance().getSelf();
            Game.instance().getCamera().getPosition().x = ((NetMessage.GameStarted) o).position.x;
            Game.instance().getCamera().getPosition().z = ((NetMessage.GameStarted) o).position.z;
            player.setPosition(Game.instance().getCamera().getPosition());
            player.setRole(((NetMessage.GameStarted) o).role);
            player.setColor(((NetMessage.GameStarted) o).color);
            player.setTasks(((NetMessage.GameStarted) o).tasks);
            Collections.shuffle(player.getTasks());
        }
    }

    public void connect() {
        client = new Client();
        NetMessage.registerAll(client.getKryo());

        Log.i("Connected.");
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);

                List<CallbackItem> remove = new ArrayList<>();
                for (CallbackItem c : callbackItems) {
                    if (c.clazz == o.getClass()) {
                        remove.add(c);
                        c.consumer.accept(o);
                    }
                }
                callbackItems.removeAll(remove);
                handle(o);
                EventBus.getDefault().post(o);
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

    public AmongUsClient sendMessage(Object message) {
        if (client == null)
            connect();
        client.sendTCP(message);
        return this;
    }

    public <T> void awaitReply(Class<T> packet, Consumer<T> consumer) {
        CallbackItem i = new CallbackItem();
        i.clazz = packet;
        i.consumer = consumer;
        callbackItems.add(i);
    }

}
