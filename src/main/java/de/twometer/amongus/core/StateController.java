package de.twometer.amongus.core;

import de.twometer.amongus.model.GameState;

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


    private void handleChange(GameState prev, GameState next) {

    }

}
