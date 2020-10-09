package de.twometer.amongus3d.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus3d.net.CreateGameMessage;
import de.twometer.amongus3d.net.CreateGameMessageReply;
import de.twometer.amongus3d.net.JoinGameMessage;
import de.twometer.amongus3d.net.JoinGameMessageReply;
import de.twometer.amongus3d.util.Log;

import java.io.IOException;

public class ServerMain {

    public static final int PORT1 = 37832;
    public static final int PORT2 = 37833;

    public static void registerClasses(Kryo kryo) {
        kryo.register(CreateGameMessage.class);
        kryo.register(CreateGameMessageReply.class);
        kryo.register(JoinGameMessage.class);
        kryo.register(JoinGameMessageReply.class);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        registerClasses(server.getKryo());
        server.bind(PORT1, PORT2);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                if (o instanceof CreateGameMessage) {
                    Log.i("Creating game");
                    connection.sendTCP(new CreateGameMessageReply(SessionCodeGenerator.generate()));
                } else if (o instanceof JoinGameMessage) {
                    Log.i("Joining game");
                    connection.sendTCP(new JoinGameMessageReply(true));
                }
                System.out.println("receive message " + o.getClass().toString());
            }
        });
        Log.i("Server online");
    }

}
