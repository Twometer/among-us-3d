package de.twometer.amongus3d.ui.font;

public class Glyph {

    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int xOffset;
    public int yOffset;
    public int advance;

    public void setId(int id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setAdvance(int advance) {
        this.advance = advance;
    }
}
