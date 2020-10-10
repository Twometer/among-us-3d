package de.twometer.amongus3d.audio;

import de.twometer.amongus3d.io.TextureLoader;
import de.twometer.amongus3d.render.Texture;

import java.util.HashMap;
import java.util.Map;

public class SoundProvider {


    private final Map<String, SoundBuffer> cache = new HashMap<>();

    public SoundBuffer getBuffer(String path) {
        SoundBuffer buf = cache.get(path);
        if (buf == null) {
            try {
                buf = new SoundBuffer(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cache.put(path, buf);
        }
        return buf;
    }

}
