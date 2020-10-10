package de.twometer.amongus3d.audio;

import de.twometer.amongus3d.core.Game;
import org.joml.Vector3f;

import java.util.HashMap;

public class SoundFX {

    private static HashMap<String, SoundSource> srcs = new HashMap<>();
    private static HashMap<String, SoundSource> srcs_positional = new HashMap<>();

    public static void play(String fx) {
        SoundSource soundSource = srcs.get(fx);
        if (soundSource == null) {
            SoundBuffer buf = Game.instance().getSoundProvider().getBuffer("sound/" + fx + ".ogg");
            soundSource = new SoundSource(buf, false, true);
            soundSource.setPosition(new Vector3f(0, 0, 0));
            srcs.put(fx, soundSource);
        }
        soundSource.play();
    }

    public static void addPositional(String fx, Vector3f pos) {
        SoundSource soundSource = srcs_positional.get(fx);
        if (soundSource == null) {
            SoundBuffer buf = Game.instance().getSoundProvider().getBuffer("sound/" + fx + ".ogg");
            soundSource = new SoundSource(buf, true, false);
            soundSource.setPosition(pos);
            srcs_positional.put(fx, soundSource);
        }
        Game.instance().getDebug().addDebugPos(pos);
    }

    public static void setWorldRunning(boolean running) {
        for (SoundSource soundSource : srcs_positional.values())
            if (running)
                soundSource.play();
            else
                soundSource.pause();
    }

}
