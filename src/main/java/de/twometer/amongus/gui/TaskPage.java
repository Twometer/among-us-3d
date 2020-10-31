package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.net.NetMessage;

import javax.sound.sampled.FloatControl;

public class TaskPage extends BasePage {

    private final Location location;
    private final TaskType taskType;

    private final PlayerTask task;

    public TaskPage(Location location, TaskType taskType) {
        super("Tasks/" + taskType.name() + ".html");
        this.location = location;
        this.taskType = taskType;
        this.task = AmongUs.get().getSession().getMyself().findRunningTaskByStage(location, taskType);
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        if (taskType == TaskType.DataTransfer && location != Location.Admin) {
            context.call("ConfigureTaskUI", false, TaskFormatter.formatLocation(location));
        } else if (taskType == TaskType.DivertPower) {
            var destination = task.getLastStage().getLocation().name().toLowerCase();
            context.call("setSliderActive", destination);
        }
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
