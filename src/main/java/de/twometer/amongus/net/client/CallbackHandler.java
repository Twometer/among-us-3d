package de.twometer.amongus.net.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CallbackHandler {

    private static class Callback<T> {
        private final Consumer<T> consumer;
        private final Class<T> clazz;

        public Callback(Consumer<T> consumer, Class<T> clazz) {
            this.consumer = consumer;
            this.clazz = clazz;
        }
    }

    private final List<Callback> callbacks = new CopyOnWriteArrayList<>();

    public <T> void await(Class<T> clazz, Consumer<T> callback) {
        callbacks.add(new Callback<>(callback, clazz));
    }

    public void handle(Object o) {
        var doneCallbacks = new ArrayList<Callback>();
        for (var cb : callbacks) {
            if (cb.clazz == o.getClass()) {
                cb.consumer.accept(o);
                doneCallbacks.add(cb);
            }
        }
        callbacks.removeAll(doneCallbacks);
    }

}
