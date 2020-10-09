package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.model.TaskType;
import de.twometer.amongus3d.render.RenderLayer;
import de.twometer.amongus3d.util.Log;

public class TaskGameObject extends StaticGameObject {

    private final Room room;

    private final TaskType taskType;

    private Renderable fxModel;

    public TaskGameObject(String name, Renderable model, Room room, TaskType taskType) {
        super(name, model);
        this.room = room;
        this.taskType = taskType;
    }

    @Override
    public void render(RenderLayer layer) {
        super.render(layer);
        if (taskType != TaskType.Scan)
            renderFx(layer);
    }

    private void renderFx(RenderLayer layer) {
        if (fxModel != null && layer == RenderLayer.Transparency)
            fxModel.render();
    }

    public Room getRoom() {
        return room;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setFxModel(Renderable fxModel) {
        this.fxModel = fxModel;
    }

    @Override
    public String toString() {
        return String.format("TASK.%s.%s", room, taskType);
    }

    @Override
    public boolean canPlayerInteract() {
        return true;
    }

    @Override
    public void onClicked() {
        super.onClicked();
        Log.d("Clicked on " + toString());

    }
}
