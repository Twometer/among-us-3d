package de.twometer.amongus3d.util;

public class Fps {

    private int fps;

    private int frames;

    private long lastReset = System.currentTimeMillis();

    public void frame() {
        frames++;
        if (System.currentTimeMillis() - lastReset > 1000) {
            fps = frames;
            lastReset = System.currentTimeMillis();
            frames = 0;
            Log.i("FPS: " + fps);
        }
    }

    public int get() {
        return fps;
    }
}
