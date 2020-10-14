package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.client.AmongUsClient;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.PlayerTask;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.model.player.Sabotage;
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
        return isCurrentSabotage() || (isHighlighted() && Game.instance().getSelf().getRole() != Role.Impostor);
    }

    @Override
    public boolean isHighlighted() {
        return Game.instance().getSelf().canDoTask(task) || isCurrentSabotage();
    }

    @Override
    public void onClicked() {
        super.onClicked();
        PlayerTask parent = Game.instance().getSelf().getParentTask(getTask());
        if (parent == null) {
            Log.w("Can't find task parent for " + toString());
            return;
        }
        Log.d("Clicked on " + toString() + ": " + parent.toString());
        NetMessage.CompleteTask msg = new NetMessage.CompleteTask();
        msg.task = getTask();
        Game.instance().getClient().sendMessage(msg);
        parent.completeOne();
        SoundFX.play("task_complete");
    }

    private boolean isCurrentSabotage() {
        Sabotage curSabotage = Game.instance().getClient().currentSabotage;
        return curSabotage != null && curSabotage.getTask() == getTaskType();
    }
}
