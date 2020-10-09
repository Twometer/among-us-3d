package de.twometer.amongus3d.ui;

import org.joml.Vector4f;

public class LabelComponent extends Component {

    private String text;

    private float fontSize;

    public LabelComponent(int w, int h, String text, float fontSize) {
        super(w, h);
        this.text = text;
        this.fontSize = fontSize;
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        renderer.getFontRenderer().drawCentered(text, getX() + getW() / 2f, getY(), fontSize, new Vector4f(1, 1, 1, 1));
    }

    public void setText(String text) {
        this.text = text;
    }
}
