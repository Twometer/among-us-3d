package de.twometer.amongus3d.ui.component;

import de.twometer.amongus3d.ui.GuiRenderer;
import org.joml.Vector4f;

public class InputBoxComponent extends Component {

    private static int idCounter = 0;

    private static int selectedInputBox = 0;

    private int id;

    private String text = "";

    public InputBoxComponent(int w, int h) {
        super(w, h);
        id = idCounter++;
    }

    @Override
    public void onCharTyped(char c) {
        super.onCharTyped(c);
        if (selectedInputBox != id) return;
        if (c == '\b' && text.length() > 0)
            text = text.substring(0, text.length() - 1);
        else
            text += c;
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        renderer.drawRect(getX(), getY(), getW(), getH(), new Vector4f(1, 1, 1, 1));
        renderer.drawRect(getX() + 1, getY() + 1, getW() - 2, getH() - 2, new Vector4f(0, 0, 0, 1));
        if (selectedInputBox == id)
        renderer.drawRect(getX() + 10 + (int) renderer.getFontRenderer().getStringWidth(text, 0.4f), getY() + 8, 2, getH() - 16, new Vector4f(0.75f, 0.75f, 0.75f, 1));
        renderer.getFontRenderer().draw(text, getX() + 5, getY() + 10, 0.4f, new Vector4f(1, 1, 1, 1));
    }

    @Override
    public void onClick() {
        super.onClick();
        selectedInputBox = id;
    }

    public String getText() {
        return text;
    }
}
