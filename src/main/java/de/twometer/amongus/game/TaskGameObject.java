package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.gui.TaskPage;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.TaskType;
import de.twometer.neko.render.model.ModelBase;

public class TaskGameObject extends GameObject {

    private final Location location;
    private final TaskType taskType;

    public TaskGameObject(ModelBase model, Location location, TaskType taskType) {
        super(model);
        this.location = location;
        this.taskType = taskType;
    }

    @Override
    public boolean canInteract() {
        return AmongUs.get().getSession().getMyself().canDoTask(location, taskType);
    }

    public Location getLocation() {
        return location;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public boolean isHighlighted() {
        return canInteract();
    }

    @Override
    public void onClick() {
        super.onClick();
        AmongUs.get().getGuiManager().showPage(new TaskPage(location, taskType));
    }

}
