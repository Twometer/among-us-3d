package de.twometer.amongus.net.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.Config;
import de.twometer.neko.event.Events;
import de.twometer.neko.util.Log;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetClient extends Listener {

    private final Client client = new Client();
    private final NetHandler handler = new NetHandler(this);
    private final CallbackHandler callbackHandler = new CallbackHandler();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private volatile boolean connecting = false;

    public NetClient() {
        NetMessage.registerAll(client.getKryo());
    }

    public void connect() {
        if (client.isConnected())
            return;
        if (connecting) {
            Log.d("Already connecting");
            return;
        }

        connecting = true;
        var config = Config.get();

        try {
            client.start();
            client.addListener(this);
            client.connect(5000, config.getServerIp(), config.getServerPort());
        } catch (IOException e) {
            Log.e("Cannot connect to the server", e);
        }

        connecting = false;
    }

    public void disconnect() {
        client.close();
    }

    public CallbackHandler sendMessage(Object msg) {
        executor.execute(() -> {
            connect();
            if (!client.isConnected()) {
                Log.e("Failed to send message");
                callbackHandler.failAll();
                return;
            }
            client.sendTCP(msg);
        });
        return callbackHandler;
    }

    @Override
    public void connected(Connection connection) {
        Log.i("Connection established");
    }

    @Override
    public void disconnected(Connection connection) {
        Log.w("Connection lost");
        callbackHandler.failAll();
    }

    @Override
    public void received(Connection connection, Object o) {
        callbackHandler.handle(o);
        handler.handle(o);
        Events.post(o);
    }

    public boolean isConnected() {
        return client.isConnected();
    }
}
