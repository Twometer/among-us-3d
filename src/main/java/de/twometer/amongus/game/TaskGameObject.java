package de.twometer.amongus.game;

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
        return true;
    }

}
