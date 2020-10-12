package de.twometer.amongus3d;

import de.twometer.amongus3d.audio.OpenAL;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.util.Log;

public class Bootstrap {

    public static void main(String[] args) {
        Log.i("Starting Among Us 3D...");
        Log.i(" Original by Innersloth");
        Log.i(" 3D ver made by Twometer");
        Log.i("The only game where you lose your friends in less than 5 minutes");
        Game.instance().run();
        Log.i("Shutting down...");
        OpenAL.close();
        System.exit(0);
    }

}
