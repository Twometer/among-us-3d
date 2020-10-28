package de.twometer.amongus.gui;

import de.twometer.amongus.game.AmongUs;
import de.twometer.neko.sound.SoundSource;
import de.twometer.neko.util.MathF;

import java.util.HashMap;
import java.util.Map;

public class ApiGui {

    private static int idCounter = 0;
    private static final Map<Integer, SoundSource> sources = new HashMap<>();

    public static void reset() {
        for (var src : sources.values())
            src.stop();
    }

    public int playSoundRandom(String sound, int n) {
        var rand = (int) (MathF.rand() * n) + 1;
        return saveSource(
                AmongUs.get().getSoundFX().play(sound + rand + ".ogg")
        );
    }

    public int playSoundLooping(String sound) {
        return saveSource(
                AmongUs.get().getSoundFX().sourceBuilder(sound)
                        .setAbsolute(false)
                        .setLooping(true)
                        .play()
        );
    }

    public int playSoundPitched(String sound, float pitch) {
        return saveSource(
                AmongUs.get().getSoundFX().sourceBuilder(sound)
                        .setAbsolute(false)
                        .setPitch(pitch)
                        .play()
        );
    }

    public int playSound(String sound) {
        return saveSource(
                AmongUs.get().getSoundFX().play(sound)
        );
    }

    public void stopSource(int id) {
        var src = sources.get(id);
        if (src != null) {
            src.setLooping(false);
            src.stop();
        }
    }

    private int saveSource(SoundSource src) {
        var id = idCounter++;
        sources.put(id, src);
        return id;
    }

}
