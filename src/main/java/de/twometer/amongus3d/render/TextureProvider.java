package de.twometer.amongus3d.render;

import de.twometer.amongus3d.io.TextureLoader;

import javax.xml.soap.Text;
import java.util.HashMap;
import java.util.Map;

public class TextureProvider {

    private final Map<String, Texture> cache = new HashMap<>();

    public Texture getTexture(String path) {
        Texture tex = cache.get(path);
        if (tex == null) {
            tex = TextureLoader.loadTexture(path);
            cache.put(path, tex);
        }
        return tex;
    }

}
