package de.twometer.amogus.model

enum class Location(val footstepSound: FootstepSound, val interactCheckExempt: Boolean = false) {
    Reactor(FootstepSound.Glass),
    UpperEngine(FootstepSound.Glass),
    LowerEngine(FootstepSound.Glass),
    Security(FootstepSound.Tile, true),
    MedBay(FootstepSound.Tile),
    Electrical(FootstepSound.Tile),
    Storage(FootstepSound.Glass),
    Cafeteria(FootstepSound.Tile),
    Admin(FootstepSound.Carpet),
    Comms(FootstepSound.Tile),
    Shields(FootstepSound.Tile),
    O2(FootstepSound.Tile, true),
    Navigation(FootstepSound.Tile),
    Weapons(FootstepSound.Tile),
    Hallways(FootstepSound.Metal),
    Invalid(FootstepSound.Metal)
}