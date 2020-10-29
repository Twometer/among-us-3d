package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.TaskType;

public class TaskPage extends BasePage {

    public TaskPage(TaskType taskType) {
        super("Tasks/" + taskType.name() + ".html");
    }

    public void taskComplete() {
        AmongUs.get().getSoundFX().play("TaskComplete.ogg");
        close();
    }

    public void close() {
        AmongUs.get().getScheduler().runLater(800, this::goBack);
    }

}
