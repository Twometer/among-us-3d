package de.twometer.amongus.model;

public enum TaskType {
    // General
    AcceptPower,
    DataTransfer,
    FixWiring,
    ClearGarbage,

    // Electrical
    DivertPower,

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
