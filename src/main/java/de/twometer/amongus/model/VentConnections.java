package de.twometer.amongus.model;

public interface VentConnections {

    Location[][] VENTS = new Location[][]{
            {Location.UpperEngine, Location.Reactor},
            {Location.LowerEngine, Location.Reactor},
            {Location.Security, Location.MedBay, Location.Electrical},
            {Location.Weapons, Location.Navigation},
            {Location.Shields, Location.Navigation},
            {Location.Cafeteria, Location.Hallways, Location.Admin}
    };

}
