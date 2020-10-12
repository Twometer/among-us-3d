package de.twometer.amongus3d.server;

import de.twometer.amongus3d.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scheduler {

    private long idc = 0;

    private static class SchedulerItem {
        long id = 0;
        long time;
        Runnable runnable;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchedulerItem item = (SchedulerItem) o;
            return id == item.id &&
                    time == item.time &&
                    Objects.equals(runnable, item.runnable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, time, runnable);
        }
    }

    private List<SchedulerItem> items = new CopyOnWriteArrayList<>();

    public long runLater(long delay, Runnable runnable) {
        SchedulerItem item = new SchedulerItem();
        item.id = idc++;
        item.time = System.currentTimeMillis() + delay;
        item.runnable = runnable;
        Log.d("Scheduling job with d=" + delay + " and id=" + item.id);
        items.add(item);
        return item.id;
    }

    public void cancel(long jobId) {
        items.removeIf(item -> item.id == jobId);
    }

    public void init() {
        new Thread(() -> {
            try {
                while (true) {

                    for (SchedulerItem item : items) {
                        if (System.currentTimeMillis() >= item.time) {
                            Log.d("Running job #" + item.id + " since time's up");
                            item.runnable.run();
                            items.remove(item);
                            break;
                        }
                    }

                    Thread.sleep(1);
                }
            } catch (Exception e) {
                Log.w("Scheduler offline");
            }
        }).start();
    }

}
