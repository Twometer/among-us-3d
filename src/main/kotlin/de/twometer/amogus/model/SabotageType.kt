package de.twometer.amogus.model

enum class SabotageType(val critical: Boolean) {
    O2(true),
    Lights(false),
    Reactor(true),
    Comms(false)
}