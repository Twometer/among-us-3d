package de.twometer.amongus.net.server;

import com.esotericsoftware.kryonet.Server;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.Config;
import de.twometer.neko.util.Log;

import java.io.IOException;

public class AmongUsServer {

    private final Server server = new Server();

    public AmongUsServer() {
        NetMessage.registerAll(server.getKryo());
    }

    public void launch() throws IOException {
        Log.i("Starting Among Us 3D server...");

        var config = Config.get();

        server.start();
        server.bind(config.getServerPort());
        Log.i("Binding to *:" + config.getServerPort());
    }

}
