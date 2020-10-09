package de.twometer.amongus3d.model.world;

public enum TaskType {
    // General
    EnergyDist,
    DataTransfer,
    FixWiring,
    ClearGarbage,

    // Med bay
    Scan,
    InspectSamples,

    // Weapons
    Shoot,

    // Reactor
    UnlockManifold,
    StartReactor,

    // Sabotages
    FixLights,
    FixComms,
    FixO2Depletion,
    FixMeltdown,

    // Navigation
    StabilizeSteering,
    ChartCourse,

    // Admin
    SwipeCard,

    // Engines
    RefuelEngine,
    EngineMgmt,

    // Shields
    PrimeShields;

    public static TaskType parse(String id) {
        for (TaskType t : TaskType.values())
            if (t.name().equals(id))
                return t;
        throw new IllegalArgumentException("Unknown task type " + id);
    }
}
