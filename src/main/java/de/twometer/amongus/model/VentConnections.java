package de.twometer.amongus.model;

import java.util.Arrays;

public final class VentConnections {

    private static final Location[][] ventLocations = new Location[][]{
            {Location.UpperEngine, Location.Reactor},
            {Location.LowerEngine, Location.Reactor},
            {Location.Security, Location.MedBay, Location.Electrical},
            {Location.Weapons, Location.Navigation},
            {Location.Shields, Location.Navigation},
            {Location.Cafeteria, Location.Hallways, Location.Admin}
    };

    public static Location[] getVentEnds(Location start) {
        for (var locations : ventLocations) {
            if (Arrays.stream(locations).anyMatch(l -> l == start))
                return locations;
        }
        return null;
    }

}
