package de.twometer.amongus3d.model;

public enum Room {
    Reactor,
    UpperEngine,
    LowerEngine,
    Security,
    MedBay,
    Electrical,
    Storage,
    Cafeteria,
    Admin,
    Comms,
    Shields,
    O2,
    Navigation,
    Weapons,
    ShieldsO2;  // That one fucking vent is between shields and O2

    public static Room parse(String id) {
        for (Room room : Room.values())
            if (room.name().equals(id))
                return room;
        throw new IllegalArgumentException("Unknown room " + id);
    }
}
