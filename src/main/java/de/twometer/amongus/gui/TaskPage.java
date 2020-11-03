package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.util.RandomUtil;

import javax.sound.sampled.FloatControl;
import java.util.Random;

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
        } else if (taskType == TaskType.Scan) {
            var player = amongUs.getSession().getMyself();
            var id = Integer.toHexString(player.id ^ hashCode()).substring(0, 6).toUpperCase();
            var height = RandomUtil.nextInt(55, 70);
            var weight = RandomUtil.nextInt(35, 55);
            var bt = new String[]{"A", "B", "AB", "0"}[RandomUtil.nextInt(0, 4)];
            var state = player.alive ? "HEALTHY" : "GHOST";
            context.call("setProperties", id, player.color.name(), height, weight, bt, state);
        } else if (taskType == TaskType.InspectSamples && task.isTimerEnded()) {
            context.call("setAnomaly", RandomUtil.nextInt(0, 5));
        }
    }

    public void beginTimeout() {
        task.startTimer(55);
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
        AmongUs.get().getScheduler().runLater(800, () -> {
            if (AmongUs.get().getStateController().isRunning())
                this.goBack();
        });
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
