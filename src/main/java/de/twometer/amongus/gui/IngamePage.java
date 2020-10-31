package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.event.UpdateEvent;
import de.twometer.amongus.model.GameState;
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
            if (task.isTimerRunning()) state = 1;
            if (task.isCompleted()) state = 2;

            context.call("addTask", task.toString(), state);
        }
        setProgress(amongUs.getSession().taskProgress);
        amongUs.getStateController().changeState(GameState.Ingame);
    }

    private long lastUpdate = System.currentTimeMillis();

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (System.currentTimeMillis() - lastUpdate > 1000) {

            var idx = 0;
            for (var task : amongUs.getSession().getMyself().tasks) {
                if (task.isTimerRunning()) {
                    context.call("setTask", idx, task.toString());
                }
                idx++;
            }

            lastUpdate = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onProgressChanged(NetMessage.OnTaskProgressChanged progressChanged) {
        amongUs.getScheduler().run(() -> setProgress(progressChanged.progress));
    }

    private void setProgress(float progress) {
        context.call("setTaskProgress", progress);
    }

    @Subscribe
    public void onKeyPress(KeyPressedEvent event) {
        if (event.key == GLFW.GLFW_KEY_TAB) {
            tasksVisible = !tasksVisible;
            context.call("setTasksVisible", tasksVisible);
        }
    }

}

