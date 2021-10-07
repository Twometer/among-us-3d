package de.twometer.amogus.model

enum class Location(val footstepSound: FootstepSound) {
    Reactor(FootstepSound.Tile),
    UpperEngine(FootstepSound.Glass),
    LowerEngine(FootstepSound.Glass),
    Security(FootstepSound.Tile),
    MedBay(FootstepSound.Tile),
    Electrical(FootstepSound.Tile),
    Storage(FootstepSound.Glass),
    Cafeteria(FootstepSound.Tile),
    Admin(FootstepSound.Carpet),
    Comms(FootstepSound.Tile),
    Shields(FootstepSound.Tile),
    O2(FootstepSound.Tile),
    Navigation(FootstepSound.Tile),
    Weapons(FootstepSound.Tile),
    Hallways(FootstepSound.Metal)
}