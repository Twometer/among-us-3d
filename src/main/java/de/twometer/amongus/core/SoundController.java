package de.twometer.amongus.core;

import de.twometer.amongus.event.StateChangeEvent;
import de.twometer.amongus.model.GameState;
import de.twometer.neko.event.Events;
import de.twometer.neko.sound.SoundFX;
import de.twometer.neko.sound.SoundSource;
import de.twometer.neko.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SoundController {

    private SoundFX sfx;
    private SoundSource menuSound;
    private final List<SoundSource> worldSounds = new ArrayList<>();

    public void initialize() {
        Events.register(this);
        sfx = AmongUs.get().getSoundFX();
    }

    private SoundSource playAmbiance(String amb, float x, float y, float z) {
        var src = sfx.sourceBuilder("Ambiance/" + amb)
                .setAbsolute(true)
                .setPosition(new Vector3f(x, y, z))
                .setLooping(true)
                .play();
        worldSounds.add(src);
        return src;
    }

    private void setWorldRunning(boolean worldRunning) {
        if (worldRunning) {
            worldSounds.clear();
            playAmbiance("Engines.ogg", 9, 0.25f, -8.15f).setRolloffFactor(5);
            playAmbiance("Reactor.ogg", 3.58f, 0.75f, -15.25f);
            playAmbiance("Engines.ogg", 9, 0.25f, -22.55f).setRolloffFactor(5);
            playAmbiance("Security.ogg", 14.4f, 0.25f, -18.11f).setRolloffFactor(2);
            playAmbiance("Medbay.ogg", 20.36f, 0.0f, -19.07f).setRolloffFactor(3.5f);
            playAmbiance("Medbay.ogg", 16.86f, 0.0f, -19.07f).setRolloffFactor(3.5f);
            playAmbiance("Electrical.ogg", 19.0f, 0.25f, -9.87f);
            playAmbiance("Storage.ogg", 26.91f, 0.25f, -6.72f).setRolloffFactor(0.5f);
            playAmbiance("Comms.ogg", 33.50f, 0.625f, -1.61f);
            playAmbiance("Shields.ogg", 38.4f, 0.15f, -8.72f);
            playAmbiance("Shields.ogg", 42.0f, 0.15f, -8.20f);
            playAmbiance("Admin.ogg", 34.25f, 0.5f, -11.86f);
            playAmbiance("Weapons.ogg", 39.3f, 0.625f, -22.85f);
            playAmbiance("Cafeteria.ogg", 28.18f, 5.0f, -23.57f);
            playAmbiance("O2.ogg", 36.63f, 0.0f, -18.6f);
            playAmbiance("Hallways.ogg", 40.18f, 0.125f, -15.86f);
            playAmbiance("Hallways.ogg", 31.7f, 0.3f, -7.6f);
        } else {
            for (var sound : worldSounds)
                sound.stop();
        }
    }

    @Subscribe
    public void onGameStateChanged(StateChangeEvent event) {
        if (isMenu(event.getNext())) {
            setWorldRunning(false);
            if (menuSound == null || !menuSound.isPlaying())
                menuSound = sfx.sourceBuilder("MainTheme.ogg")
                        .setAbsolute(false)
                        .setLooping(true)
                        .play();
        } else {
            if (!isMenu(event.getNext())) {
                menuSound.stop();
                Log.i("* holy music stops *");
            }
            setWorldRunning(event.getNext() == GameState.Ingame);
        }
    }

    private boolean isMenu(GameState state) {
        return state == GameState.Lobby || state == GameState.Menus;
    }

}
