package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.ui.component.Component;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen extends Component {

    private List<Component> components = new ArrayList<>();

    public void addComponent(Component component) {
        components.add(component);
    }

    public GuiScreen() {
        super(Game.instance().getWindow().getWidth(), Game.instance().getWindow().getHeight());
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void dummy(NoSubscriberEvent event) {

    }

    public void onHide() {
        EventBus.getDefault().unregister(this);
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

    public void onShown() {

    }

    public boolean renderStarfield() {
        return true;
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        for (Component component : components)
            component.render(renderer);
    }
}
