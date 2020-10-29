package de.twometer.amongus.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    private static class Task {

        private static final AtomicInteger idCounter = new AtomicInteger(0);

        private final int id;

        private final long time;

        private final Runnable runnable;

        private Task(long time, Runnable runnable) {
            this.id = idCounter.incrementAndGet();
            this.time = time;
            this.runnable = runnable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return id == ((Task) o).id;
        }

        @Override
        public int hashCode() {
            return id;
        }

    }

    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    public Task run(Runnable runnable) {
        return runLater(0, runnable);
    }

    public Task runLater(long delay, Runnable runnable) {
        var task = new Task(System.currentTimeMillis() + delay, runnable);
        tasks.add(task);
        return task;
    }

    public void cancel(Task task) {
        tasks.remove(task);
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
