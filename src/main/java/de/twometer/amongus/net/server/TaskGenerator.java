package de.twometer.amongus.net.server;

import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.TaskStage;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.util.RandomUtil;

import java.util.List;

import static de.twometer.amongus.util.RandomUtil.getRandomItem;

public class TaskGenerator {

    private static final List<Location> WIRING_LOCATIONS = List.of(Location.Cafeteria, Location.Electrical, Location.Navigation, Location.Security, Location.Storage);

    private static final List<Location> ACCEPT_POWER_LOCATIONS = List.of(Location.Navigation, Location.Security, Location.Reactor, Location.Weapons, Location.O2, Location.Comms);

    private static final List<Location> DOWNLOAD_LOCATIONS = List.of(Location.Comms, Location.Navigation, Location.Electrical, Location.Cafeteria, Location.Weapons);

    private static final List<Location> GARBAGE_LOCATIONS = List.of(Location.Storage, Location.O2, Location.Cafeteria);

    private static final List<TaskType> COMMON_TASKS = List.of(
            TaskType.SwipeCard,
            TaskType.FixWiring
    );

    private static final List<TaskType> SHORT_TASKS = List.of(
            TaskType.EngineMgmt,
            TaskType.ChartCourse,
            TaskType.DivertPower,
            TaskType.StabilizeSteering,
            TaskType.PrimeShields,
            TaskType.UnlockManifold,
            TaskType.DataTransfer
    );

    private static final List<TaskType> LONG_TASKS = List.of(
            TaskType.Scan,
            TaskType.StartReactor,
            TaskType.InspectSamples,
            TaskType.ClearGarbage,
            TaskType.Shoot
    );

    public static PlayerTask newCommonTask() {
        return expand(getRandomItem(COMMON_TASKS));
    }

    public static PlayerTask newShortTask() {
        return expand(getRandomItem(SHORT_TASKS));
    }

    public static PlayerTask newLongTask() {
        return expand(getRandomItem(LONG_TASKS));
    }

    private static PlayerTask expand(TaskType taskType) {
        switch (taskType) {
            case SwipeCard:
                return new PlayerTask.Builder()
                        .addStage(new TaskStage(Location.Admin, TaskType.SwipeCard))
                        .build();
            case FixWiring:
                var builder = new PlayerTask.Builder();
                var locations = RandomUtil.getUniqueRandom(WIRING_LOCATIONS, 3);
                for (var loc : locations)
                    builder.addStage(new TaskStage(loc, TaskType.FixWiring));
                return builder.build();
            case EngineMgmt:
                return new PlayerTask.Builder()
                        .addStage(Location.UpperEngine, TaskType.EngineMgmt)
                        .addStage(Location.LowerEngine, TaskType.EngineMgmt)
                        .build();
            case ChartCourse:
                return makeSingleStageTask(Location.Navigation, TaskType.ChartCourse);
            case DivertPower:
                return new PlayerTask.Builder()
                        .addStage(Location.Electrical, TaskType.DivertPower)
                        .addStage(getRandomItem(ACCEPT_POWER_LOCATIONS), TaskType.AcceptPower)
                        .build();
            case StabilizeSteering:
                return makeSingleStageTask(Location.Navigation, TaskType.StabilizeSteering);
            case PrimeShields:
                return makeSingleStageTask(Location.Shields, TaskType.PrimeShields);
            case UnlockManifold:
                return makeSingleStageTask(Location.Reactor, TaskType.UnlockManifold);
            case DataTransfer:
                return new PlayerTask.Builder()
                        .addStage(getRandomItem(DOWNLOAD_LOCATIONS), TaskType.DataTransfer)
                        .addStage(Location.Admin, TaskType.DataTransfer)
                        .build();
            case Scan:
                return makeSingleStageTask(Location.MedBay, TaskType.Scan);
            case StartReactor:
                return makeSingleStageTask(Location.Reactor, TaskType.StartReactor);
            case InspectSamples:
                return makeSingleStageTask(Location.MedBay, TaskType.InspectSamples);
            case ClearGarbage:
                var garbageLocations = RandomUtil.getUniqueRandom(GARBAGE_LOCATIONS, 2).toArray(new Location[0]);
                return new PlayerTask.Builder()
                        .addStage(garbageLocations[0], TaskType.ClearGarbage)
                        .addStage(garbageLocations[1], TaskType.ClearGarbage)
                        .build();
            case Shoot:
                return makeSingleStageTask(Location.Weapons, TaskType.Shoot);
        }

        throw new IllegalArgumentException("Unknown task type " + taskType);
    }

    private static PlayerTask makeSingleStageTask(Location loc, TaskType type) {
        return new PlayerTask.Builder()
                .addStage(loc, type)
                .build();
    }

}

