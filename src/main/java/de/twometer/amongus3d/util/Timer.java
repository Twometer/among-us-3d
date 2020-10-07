package de.twometer.amongus3d.util;

public class Timer {

    private final long delay;

    private long lastReset;

    public Timer(int tps) {
        this.delay = 1000 / tps;
        reset();
    }

    public void reset() {
        lastReset = System.currentTimeMillis();
    }

    public boolean elapsed() {
        return System.currentTimeMillis() - lastReset > delay;
    }

}
