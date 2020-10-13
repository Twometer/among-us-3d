package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskType;

public enum Sabotage {
    None(null),
    O2(TaskType.FixO2Depletion),
    Lights(TaskType.FixLights),
    Reactor(TaskType.FixMeltdown),
    Comms(TaskType.FixComms);

    private final TaskType task;

    Sabotage(TaskType task) {
        this.task = task;
    }

    public TaskType getTask() {
        return task;
    }
}
