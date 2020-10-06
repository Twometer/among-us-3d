package de.twometer.amongus3d;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.util.Log;

public class Bootstrap {

    public static void main(String[] args) {
        Log.i("Starting Among Us 3D...");
        Game.instance().run();
        Log.i("Shutting down...");
    }

}
