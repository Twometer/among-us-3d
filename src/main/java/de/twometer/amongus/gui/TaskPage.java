package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.amongus.model.TaskType;

public class TaskPage extends BasePage {

    public TaskPage(TaskType taskType) {
        super("Tasks/" + taskType.name() + ".html");
    }

    public void taskComplete() {
        AmongUs.get().getSoundFX().play("TaskComplete.ogg");
        AmongUs.get().getScheduler().runLater(800, this::goBack);
    }

}
