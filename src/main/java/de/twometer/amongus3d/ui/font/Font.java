package de.twometer.amongus3d.ui.font;

import de.twometer.amongus3d.render.Texture;

import java.util.HashMap;
import java.util.Map;

public class Font {

    private Map<Integer, Glyph> glyphs = new HashMap<>();

    private Texture texture;

    public void setGlyphs(Map<Integer, Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Map<Integer, Glyph> getGlyphs() {
        return glyphs;
    }

    public Texture getTexture() {
        return texture;
    }
}
