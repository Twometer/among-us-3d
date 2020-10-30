package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.net.NetMessage;

public class TaskPage extends BasePage {

    private final PlayerTask task;

    public TaskPage(Location location, TaskType taskType) {
        super("Tasks/" + taskType.name() + ".html");
        this.task = AmongUs.get().getSession().getMyself().findRunningTaskByStage(location, taskType);
    }

    public void taskComplete() {
        task.advance();
        if (task.isCompleted()) {
            AmongUs.get().getSoundFX().play("TaskComplete.ogg");
        } else {
            AmongUs.get().getSoundFX().play("TaskProgress.ogg");
        }
        AmongUs.get().getClient().sendMessage(new NetMessage.CompleteTaskStage());
        close();
    }

    public void close() {
        AmongUs.get().getScheduler().runLater(800, this::goBack);
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
