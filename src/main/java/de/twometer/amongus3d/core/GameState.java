package de.twometer.amongus3d.core;

public class GameState {

    private State currentState;

    public enum State {
        Loading,
        Menu,
        Lobby,
        Running,
        Emergency
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;

        if (currentState == State.Running)
            Game.instance().getWindow().setCursorVisible(false);
        else
            Game.instance().getWindow().setCursorVisible(true);
    }
}
