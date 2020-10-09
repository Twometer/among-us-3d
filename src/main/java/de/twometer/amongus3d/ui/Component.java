package de.twometer.amongus3d.ui;

public abstract class Component {

    private int x;
    private int y;
    private int w;
    private int h;

    public Component(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void onClick() {

    }

    public void onCharTyped(char c) {

    }

    public void render(GuiRenderer renderer) {

    }

    public boolean isMouseOver(int x, int y) {
        return x > this.x && y > this.y && x < this.x + w && y < this.y + h;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
