package de.twometer.amongus.core;

import de.twometer.amongus.model.GameState;
import de.twometer.neko.util.Log;

public class StateController {

    private GameState state = GameState.Menus;

    public void changeState(GameState state) {
        var prev = this.state;
        this.state = state;
        handleChange(prev, state);
    }

    public GameState getState() {
        return state;
    }

    public boolean isRunning() {
        return state == GameState.Ingame;
    }

    private void handleChange(GameState prev, GameState next) {
        Log.i("Changed state from " + prev + " to " + next);
    }

}
