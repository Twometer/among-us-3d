package de.twometer.amongus.net.server;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HandlerRegistry {

    private final Map<Class, PacketHandler> map = new HashMap<>();

    public <T> void register(Class<T> clazz, PacketHandler<T> handler) {
        map.put(clazz, handler);
    }

    public void handle(PlayerConnection c, Object o) {
        var handler = map.get(o.getClass());
        if (handler == null) return;
        handler.handle(c, o);
    }

}
