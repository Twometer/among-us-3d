package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.event.KeyPressedEvent;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class IngamePage extends BasePage {

    private boolean tasksVisible = true;

    public IngamePage() {
        super("Ingame.html");
        AmongUs.get().getWindow().setCursorPosition(new Vector2f(
                AmongUs.get().getWindow().getWidth() / 2.0f,
                AmongUs.get().getWindow().getHeight() / 2.0f
        ));
    }

    @Override
    public boolean isCursorVisible() {
        return false;
    }

    @Override
    public void onDomReady() {
        super.onDomReady();

        for (var task : amongUs.getSession().getMyself().tasks) {
            var state = task.getProgress() == 0 ? 0 : 1;
            if (task.isCompleted()) state = 2;

            context.call("addTask", task.toString(), state);
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

