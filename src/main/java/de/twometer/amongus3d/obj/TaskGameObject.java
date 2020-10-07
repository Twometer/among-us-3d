package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.IRenderable;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.model.TaskType;

public class TaskGameObject extends StaticGameObject {

    private final Room room;

    private final TaskType taskType;

    private final String ext;

    public TaskGameObject(String name, IRenderable model, Room room, TaskType taskType, String ext) {
        super(name, model);
        this.room = room;
        this.taskType = taskType;
        this.ext = ext;
    }

    @Override
    public String toString() {
        return String.format("TASK.%s.%s.%s", room, taskType, ext);
    }

    @Override
    public boolean canPlayerInteract() {
        return true;
    }
}
