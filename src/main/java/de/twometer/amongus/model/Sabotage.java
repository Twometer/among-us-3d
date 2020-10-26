package de.twometer.amongus.model;

public enum Sabotage {
    O2(TaskType.FixO2Depletion),
    Lights(TaskType.FixLights),
    Reactor(TaskType.FixMeltdown),
    Comms(TaskType.FixComms);

    private final TaskType fixLocation;

    Sabotage(TaskType fixLocation) {
        this.fixLocation = fixLocation;
    }

    public TaskType getFixLocation() {
        return fixLocation;
    }

}
