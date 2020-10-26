package de.twometer.amongus.gui;

import de.twometer.amongus.model.TaskType;

public class TaskPage extends BasePage {

    public TaskPage(TaskType taskType) {
        super("Tasks/" + taskType.name() + ".html");
    }

}
