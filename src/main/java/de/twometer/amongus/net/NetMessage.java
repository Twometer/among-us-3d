package de.twometer.amongus.net;

import com.esotericsoftware.kryo.Kryo;
import de.twometer.amongus.model.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

public final class NetMessage {

    private NetMessage() {
    }

    private static final Class<?>[] classes = new Class<?>[]{
            // Messages
            Join.class,

            // Game model
            Location.class,
            PlayerColor.class,
            PlayerRole.class,
            PlayerTask.class,
            TaskStage.class,
            TaskType.class,
            Sabotage.class,

            // Basics
            ArrayList.class,
            HashMap.class,
            Vector3f.class
    };

    public static void registerAll(Kryo kryo) {
        for (Class<?> clazz : classes)
            kryo.register(clazz);
    }

    public static class Join {

    }

}
