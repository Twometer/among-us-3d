package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskDef;
import de.twometer.amongus3d.model.world.TaskType;
import de.twometer.amongus3d.render.RenderLayer;
import de.twometer.amongus3d.util.Log;

public class TaskGameObject extends StaticGameObject {

    private final TaskDef task;

    private Renderable fxModel;

    public TaskGameObject(String name, Renderable model, Room room, TaskType taskType) {
        super(name, model);
        this.task = new TaskDef(room, taskType);
    }

    @Override
    public void render(RenderLayer layer) {
        super.render(layer);
        if (task.getTaskType() != TaskType.Scan)
            renderFx(layer);
    }

    private void renderFx(RenderLayer layer) {
        if (fxModel != null && layer == RenderLayer.Transparency)
            fxModel.render();
    }

    public Room getRoom() {
        return task.getRoom();
    }

    public TaskType getTaskType() {
        return task.getTaskType();
    }

    public TaskDef getTask() {
        return task;
    }

    public void setFxModel(Renderable fxModel) {
        this.fxModel = fxModel;
    }

    @Override
    public String toString() {
        return String.format("TASK.%s.%s", getRoom(), getTaskType());
    }

    @Override
    public boolean canPlayerInteract() {
        return isHighlighted() && Game.instance().getSelf().getRole() != Role.Impostor;
    }

    @Override
    public boolean isHighlighted() {
        return Game.instance().getSelf().canDoTask(task);
    }

    @Override
    public void onClicked() {
        super.onClicked();
        Log.d("Clicked on " + toString());

    }
}
