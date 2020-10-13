package de.twometer.amongus3d.server;

import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.PlayerTask;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskDef;
import de.twometer.amongus3d.model.world.TaskType;

import java.util.List;
import java.util.Random;

public class TaskGenerator {

    // Sabotages
//    "Electrical.FixLights",
//    "Comms.FixComms",
//    "Admin.FixO2Depletion",
//    "O2.FixO2Depletion",
//    "Reactor.FixMeltdown",
//    "Reactor.FixMeltdown",


    public static final TaskDef[] ENERGY_DIVERT = convert(new String[]{
            "Electrical.EnergyDist",
    });

    public static final TaskDef[] ENERGY_ACCEPT = convert(new String[]{
            "Navigation.EnergyDist",
            "Security.EnergyDist",
            "Reactor.EnergyDist",
            "Weapons.EnergyDist",
            "O2.EnergyDist",
            "Comms.EnergyDist"
    });

    public static final TaskDef[] DATA_UPLOAD = convert(new String[]{
            "Admin.DataTransfer",
    });

    public static final TaskDef[] DATA_DOWNLOAD = convert(new String[]{
            "Comms.DataTransfer",
            "Navigation.DataTransfer",
            "Electrical.DataTransfer",
            "Cafeteria.DataTransfer",
            "Weapons.DataTransfer",
    });

    public static final TaskDef[] WIRING = convert(new String[]{
            "Security.FixWiring",
            "Navigation.FixWiring",
            "Electrical.FixWiring",
            "Cafeteria.FixWiring",
            "Storage.FixWiring"
    });

    public static final TaskDef[] SHORT_TASKS = convert(new String[]{
            "Weapons.Shoot",
            "Reactor.UnlockManifold",
            "Navigation.ChartCourse",
            "Admin.SwipeCard",
            "Navigation.StabilizeSteering",
            "Reactor.StartReactor",
            "Reactor.UnlockManifold",
            "Storage.ClearGarbage",
            "O2.ClearGarbage",
            "Cafeteria.ClearGarbage",
            "MedBay.Scan",
            "MedBay.InspectSamples",
            "Shields.PrimeShields",});

    private static final Random r = new Random();

    public static TaskDef genUnique(Player p, TaskDef[] arr) {
        TaskDef def;
        int tries = 0;
        do {
            def = arr[r.nextInt(arr.length)];
            tries++;
        } while (hasTask(p, def) && tries < 10);
        return def;
    }

    private static boolean hasTask(Player p, TaskDef d) {
        if (p == null) return false;
        for (PlayerTask task : p.getTasks())
            if (task.getTasks().contains(d))
                return true;
        return false;
    }

    private static TaskDef[] convert(String[] str) {
        TaskDef[] defs = new TaskDef[str.length];
        for (int i = 0; i < str.length; i++) {
            String[] s = str[i].split("\\.");
            String room = s[0];
            String task = s[1];
            defs[i] = new TaskDef(Room.parse(room), TaskType.parse(task));
        }
        return defs;
    }

}
