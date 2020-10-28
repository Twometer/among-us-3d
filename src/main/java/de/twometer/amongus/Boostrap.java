package de.twometer.amongus;

import de.twometer.amongus.game.AmongUs;
import de.twometer.amongus.server.Server;

public final class Boostrap {

    public static void main(String[] args) {
        System.out.println("    ___                                   __  __        _____ ____ \n" +
                "   /   |  ____ ___  ____  ____  ____ _   / / / /____   |__  // __ \\\n" +
                "  / /| | / __ `__ \\/ __ \\/ __ \\/ __ `/  / / / / ___/    /_ </ / / /\n" +
                " / ___ |/ / / / / / /_/ / / / / /_/ /  / /_/ (__  )   ___/ / /_/ / \n" +
                "/_/  |_/_/ /_/ /_/\\____/_/ /_/\\__, /   \\____/____/   /____/_____/  \n" +
                "                             /____/                                \n\n");

        if (args.length > 0 && args[0].equals("--server")) {
            Server.launch();
        } else {
            AmongUs.launch();
        }
    }

}
