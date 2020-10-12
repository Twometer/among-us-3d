package de.twometer.amongus3d.core;

import de.twometer.amongus3d.audio.SoundBuffer;
import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.audio.SoundSource;
import de.twometer.amongus3d.util.Log;
import org.joml.Vector3f;

public class GameState {

    private State currentState;

    private SoundSource soundSource;

    public enum State {
        Loading,
        Menu,
        Lobby,
        Running,
        Emergency
    }

    public void init() {
        SoundBuffer buffer = Game.instance().getSoundProvider().getBuffer("sound/menu.ogg");
        soundSource = new SoundSource(buffer, true, true);
        soundSource.setPosition(new Vector3f(0, 0, 0));
        soundSource.play();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
        Log.d("Game state " + currentState);

        if (currentState == State.Running) {
            Game.instance().getGuiRenderer().setCurrentScreen(null);
            Game.instance().getWindow().setCursorVisible(false);
        }else {
            Game.instance().getWindow().setCursorVisible(true);
        }

        if (soundSource == null)
            return;

        if (currentState == State.Menu || currentState == State.Lobby) {
            if (!soundSource.isPlaying())
                soundSource.play();
            SoundFX.setWorldRunning(false);
        } else if (currentState == State.Running) {
            soundSource.stop();
            SoundFX.setWorldRunning(true);
        } else if (currentState == State.Emergency) {
            SoundFX.setWorldRunning(false);
            SoundFX.play("emergency");
        }
    }

    public boolean isRunning() {
        return currentState == State.Running;
    }
}
