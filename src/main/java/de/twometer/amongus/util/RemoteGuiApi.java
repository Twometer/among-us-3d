package de.twometer.amongus.util;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.util.MathF;

public class RemoteGuiApi {

    public void playSoundRandom(String sound, int n) {
        var rand = (int)(MathF.rand() * n) + 1;
        AmongUs.get().getSoundFX().play(sound  + rand + ".ogg");
    }

    public void playSound(String sound) {
        AmongUs.get().getSoundFX().play(sound);
    }

}
