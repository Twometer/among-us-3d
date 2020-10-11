package de.twometer.amongus3d.audio;

import de.twometer.amongus3d.core.Game;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoundFX {

    private static final HashMap<String, SoundSource> srcs = new HashMap<>();
    private static final List<SoundSource> srcs_positional = new ArrayList<>();

    public static void play(String fx) {
        SoundSource soundSource = srcs.get(fx);
        if (soundSource == null) {
            SoundBuffer buf = Game.instance().getSoundProvider().getBuffer("sound/" + fx + ".ogg");
            soundSource = new SoundSource(buf, false, true);
            soundSource.setPosition(new Vector3f(0, 0, 0));
            soundSource.setGain(2f);
            soundSource.setRolloffFactor(0f);
            srcs.put(fx, soundSource);
        }
        soundSource.play();
    }

    public static SoundSource addPositional(String fx, Vector3f pos) {
        SoundSource soundSource;
        SoundBuffer buf = Game.instance().getSoundProvider().getBuffer("sound/" + fx + ".ogg");
        soundSource = new SoundSource(buf, true, false);
        soundSource.setPosition(pos);
        srcs_positional.add(soundSource);

        Game.instance().getDebug().addDebugPos(pos);
        return soundSource;
    }

    public static void setWorldRunning(boolean running) {
        for (SoundSource soundSource : srcs_positional)
            if (running)
                soundSource.play();
            else
                soundSource.pause();
    }

}
