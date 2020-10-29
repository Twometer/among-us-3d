package de.twometer.amongus.net.client;

import de.twometer.amongus.core.AmongUs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CallbackHandler {

    public static class Callback<T> {
        private final Consumer<T> consumer;
        private final Class<T> clazz;
        private Runnable errorHandler;

        public Callback(Consumer<T> consumer, Class<T> clazz) {
            this.consumer = consumer;
            this.clazz = clazz;
        }

        public void handleError(Runnable runnable) {
            this.errorHandler = runnable;
        }
    }

    private final List<Callback> callbacks = new CopyOnWriteArrayList<>();

    public <T> Callback<T> await(Class<T> clazz, Consumer<T> callback) {
        var cb = new Callback<>(callback, clazz);
        callbacks.add(cb);
        return cb;
    }

    void handle(Object o) {
        var doneCallbacks = new ArrayList<Callback>();
        for (var cb : callbacks) {
            if (cb.clazz == o.getClass()) {
                AmongUs.get().getScheduler().run(() -> cb.consumer.accept(o));
                doneCallbacks.add(cb);
            }
        }
        callbacks.removeAll(doneCallbacks);
    }

    void failAll() {
        for (var cb : callbacks)
            if (cb.errorHandler != null) {
                AmongUs.get().getScheduler().run(cb.errorHandler); // Run on UI thread
            }
        callbacks.clear();
    }

}
