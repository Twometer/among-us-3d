package de.twometer.amongus.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scheduler {

    private static class Task {

        private final long time;

        private final Runnable runnable;

        public Task(long time, Runnable runnable) {
            this.time = time;
            this.runnable = runnable;
        }
    }

    private List<Task> tasks = new CopyOnWriteArrayList<>();

    public void runLater(long delay, Runnable runnable) {
        tasks.add(new Task(System.currentTimeMillis() + delay, runnable));
    }

    public void update() {
        for (var task : tasks) {
            if (task.time < System.currentTimeMillis()) {
                task.runnable.run();
                tasks.remove(task);
                return;
            }
        }
    }

}
