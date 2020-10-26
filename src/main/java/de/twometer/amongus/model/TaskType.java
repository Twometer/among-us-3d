package de.twometer.amongus.model;

public enum TaskType {
    // General
    EnergyDist,
    DataTransfer,
    FixWiring,
    ClearGarbage,

    // Medbay
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
}
