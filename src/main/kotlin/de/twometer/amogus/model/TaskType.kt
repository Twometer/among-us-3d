package de.twometer.amogus.model

enum class TaskType {
    // Invalid
    Invalid,

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