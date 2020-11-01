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
    private List<SoundSource> worldSounds = new ArrayList<>();

    public void initialize() {
        Events.register(this);
        sfx = AmongUs.get().getSoundFX();

        menuSound = sfx.sourceBuilder("MainTheme.ogg")
                .setAbsolute(false)
                .setLooping(true);

        addAmbiance("Engines.ogg", 9, 0.25f, -8.15f).setRolloffFactor(5);
        addAmbiance("Reactor.ogg", 3.58f, 0.75f, -15.25f);
        addAmbiance("Engines.ogg", 9, 0.25f, -22.55f).setRolloffFactor(5);
        addAmbiance("Security.ogg", 14.4f, 0.25f, -18.11f).setRolloffFactor(2);
        addAmbiance("Medbay.ogg", 20.36f, 0.0f, -19.07f).setRolloffFactor(3.5f);
        addAmbiance("Medbay.ogg", 16.86f, 0.0f, -19.07f).setRolloffFactor(3.5f);
        addAmbiance("Electrical.ogg", 19.0f, 0.25f, -9.87f);
        addAmbiance("Storage.ogg", 26.91f, 0.25f, -6.72f).setRolloffFactor(0.5f);
        addAmbiance("Comms.ogg", 33.50f, 0.625f, -1.61f);
        addAmbiance("Shields.ogg", 38.4f, 0.15f, -8.72f);
        addAmbiance("Shields.ogg", 42.0f, 0.15f, -8.20f);
        addAmbiance("Admin.ogg", 34.25f, 0.5f, -11.86f);
        addAmbiance("Weapons.ogg", 39.3f, 0.625f, -22.85f);
        addAmbiance("Cafeteria.ogg", 28.18f, 5.0f, -23.57f);
        addAmbiance("O2.ogg", 36.63f, 0.0f, -18.6f);
        addAmbiance("Hallways.ogg", 40.18f, 0.125f, -15.86f);
        addAmbiance("Hallways.ogg", 31.7f, 0.3f, -7.6f);
    }

    private SoundSource addAmbiance(String amb, float x, float y, float z) {
        var src = sfx.sourceBuilder("Ambiance/" + amb)
                .setAbsolute(true)
                .setPosition(new Vector3f(x, y, z))
                .setLooping(true);
        worldSounds.add(src);
        return src;
    }

    private void setWorldRunning(boolean worldRunning) {
        if (worldRunning)
            for (var sound : worldSounds)
                sound.play();
        else
            for (var sound : worldSounds)
                sound.stop();
    }

    @Subscribe
    public void onGameStateChanged(StateChangeEvent event) {
        if (!isMenu(event.getPrev()) && isMenu(event.getNext())) {
            menuSound.play();
            setWorldRunning(false);
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
