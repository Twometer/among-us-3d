package de.twometer.amongus3d.io;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.ui.Font;
import de.twometer.amongus3d.ui.Glyph;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FontLoader {

    public static Font loadFont(String font, String tex) {
        Font f = new Font();
        try {
            f.setGlyphs(parse(font));
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.setTexture(Game.instance().getTextureProvider().getTexture(tex));
        return f;
    }

    private static Map<Integer, Glyph> parse(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("assets/" + file));
        Map<Integer, Glyph> glyphs = new HashMap<>();
        parseDocument(reader, glyphs);
        return glyphs;
    }

    private static void parseDocument(BufferedReader reader, Map<Integer, Glyph> glyphs) throws IOException {
        String line;
        while ((line = reader.readLine()) != null)
            if (isGlyph(line)) {
                Glyph glyph = parseGlyph(line);
                glyphs.put(glyph.id, glyph);
            }
    }

    private static boolean isGlyph(String line) {
        return line.startsWith("char ");
    }

    private static Glyph parseGlyph(String line) {
        Glyph glyph = new Glyph();
        glyph.setId(parseProperty(line, "id"));
        glyph.setX(parseProperty(line, "x"));
        glyph.setY(parseProperty(line, "y"));
        glyph.setWidth(parseProperty(line, "width"));
        glyph.setHeight(parseProperty(line, "height"));
        glyph.setxOffset(parseProperty(line, "xoffset"));
        glyph.setyOffset(parseProperty(line, "yoffset"));
        glyph.setAdvance(parseProperty(line, "xadvance"));
        return glyph;
    }

    private static int parseProperty(String line, String property) {
        String key = property + "=";
        int offset = line.indexOf(key);
        if (offset < 0)
            throw new RuntimeException("Unknown property " + property);
        String substring = line.substring(offset + key.length());
        String value = substring.substring(0, substring.indexOf(" "));
        return Integer.parseInt(value);
    }

}
