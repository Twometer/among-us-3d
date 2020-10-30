package de.twometer.amongus.gui;

import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.event.KeyPressedEvent;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;

public class IngamePage extends BasePage {

    private boolean tasksVisible = true;

    public IngamePage() {
        super("Ingame.html");
    }

    @Override
    public boolean isCursorVisible() {
        return false;
    }

    @Override
    public void onDomReady() {
        super.onDomReady();

        for (var task : amongUs.getSession().getMyself().tasks) {
            context.call("addTask", task.toString());
        }
    }

    @Subscribe
    public void onProgressChanged(NetMessage.OnTaskProgressChanged progressChanged) {
        amongUs.getScheduler().run(() -> context.call("setTaskProgress", progressChanged.progress));
    }

    @Subscribe
    public void onKeyPress(KeyPressedEvent event) {
        if (event.key == GLFW.GLFW_KEY_TAB) {
            tasksVisible = !tasksVisible;
            context.call("setTasksVisible", tasksVisible);
        }
    }

}

