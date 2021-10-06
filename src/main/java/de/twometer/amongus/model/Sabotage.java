package de.twometer.amongus.model;

public enum Sabotage {
    O2(true),
    Lights(false),
    Reactor(true),
    Comms(false);

    private final boolean critical;

    Sabotage(boolean critical) {
        this.critical = critical;
    }

    public boolean isCritical() {
        return critical;
    }
}
