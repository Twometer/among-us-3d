package de.twometer.amongus.util;

import de.twometer.neko.util.Log;

public class AsyncScheduler extends Scheduler {

    private boolean running;

    private Thread thread;

    public void start() {
        running = true;

        (thread = new Thread(() -> {
            while (running) {
                try {
                    update();
                    Thread.sleep(1);
                } catch (Throwable t) {
                    Log.e("Scheduler failed", t);
                    break;
                }
            }
        })).start();
    }

    public void stop() {
        running = false;
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            Log.e("Shutdown interrupted", e);
        }
    }

}
