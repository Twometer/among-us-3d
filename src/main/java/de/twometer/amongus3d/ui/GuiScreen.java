package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen extends Component {

    private List<Component> components = new ArrayList<>();

    public void addComponent(Component component) {
        components.add(component);
    }

    public GuiScreen() {
        super(Game.instance().getWindow().getWidth(), Game.instance().getWindow().getHeight());
    }

    public void handleClickEvent(int x, int y) {
        for (Component component : components)
            if (component.isMouseOver(x, y))
                component.onClick();
    }

    @Override
    public void onCharTyped(char c) {
        super.onCharTyped(c);
        for (Component component : components)
            component.onCharTyped(c);
    }

    public void relayout() {
        setW(Game.instance().getWindow().getWidth());
        setH(Game.instance().getWindow().getHeight());
        int y = 0;
        for (Component component : components) {
            component.setX(getW() / 2 - component.getW() / 2);
            component.setY(y);
            y += component.getH();
        }
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        for (Component component : components)
            component.render(renderer);
    }
}
