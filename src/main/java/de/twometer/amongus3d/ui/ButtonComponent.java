package de.twometer.amongus3d.ui;

import org.joml.Vector4f;

public class ButtonComponent extends Component {

    private String text;

    private Runnable clickListener;

    public ButtonComponent(int w, int h, String text) {
        super(w, h);
        this.text = text;
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        renderer.drawRect(getX(), getY(), getW(), getH(), new Vector4f(0.25f, 0.25f, 0.25f, 1.0f));
        renderer.getFontRenderer().drawCentered(text, getX() + getW() / 2f, getY(), 0.5f, new Vector4f(1, 1, 1, 1));
    }

    @Override
    public void onClick() {
        if (clickListener != null)
            clickListener.run();
    }

    public void setClickListener(Runnable clickListener) {
        this.clickListener = clickListener;
    }
}
