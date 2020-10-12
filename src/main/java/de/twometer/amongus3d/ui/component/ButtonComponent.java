package de.twometer.amongus3d.ui.component;

import de.twometer.amongus3d.ui.GuiRenderer;
import org.joml.Vector4f;

public class ButtonComponent extends Component {

    private String text;

    private Runnable clickListener;

    private Vector4f color = new Vector4f(1,1,1,1);

    public ButtonComponent(int w, int h, String text) {
        super(w, h);
        this.text = text;
    }

    public ButtonComponent setColor(Vector4f color) {
        this.color = color;
        return this;
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        renderer.drawRect(getX(), getY(), getW(), getH(), new Vector4f(0.25f, 0.25f, 0.25f, 1.0f));
        renderer.getFontRenderer().drawCentered(text, getX() + getW() / 2f, getY() - 2, 0.5f, color);
    }

    @Override
    public void onClick() {
        if (clickListener != null)
            clickListener.run();
    }

    public ButtonComponent setClickListener(Runnable clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public ButtonComponent setText(String text) {
        this.text = text;
        return this;
    }
}
