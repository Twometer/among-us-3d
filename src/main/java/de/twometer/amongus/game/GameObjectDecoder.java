package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.AnimationType;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.model.ToolType;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameObjectDecoder {

    private static class UnfinishedDoor {
        private Location location;
        private int otherHalf;
        private int id;
        private ModelBase model;
    }

    private final List<UnfinishedDoor> unfinishedDoors = new ArrayList<>();

    public GameObject decode(ModelBase model) {
        var modelName = model.getName();
        if (!modelName.contains("_")) return null;

        var modelType = modelName.substring(0, modelName.indexOf('_'));
        var args = modelName.substring(modelType.length() + 1).split("_");
        switch (modelType) {
            case "TASK": {
                var location = Location.valueOf(args[0]);
                var taskType = TaskType.valueOf(args[1]);
                var subtype = args[2];
                if (subtype.equalsIgnoreCase("FX")) {
                    for (var obj : AmongUs.get().getGameObjects()) {
                        if (obj instanceof TaskGameObject) {
                            var task = (TaskGameObject) obj;
                            if (task.getLocation() == location && task.getTaskType() == taskType) {
                                model.setTag(obj.getId());
                            }
                        }
                    }
                    return null;
                }
                return new TaskGameObject(model, location, taskType);
            }
            case "VENT": {
                var location = Location.valueOf(args[0]);
                var index = tryParseInt(args[1], 1);
                return new VentGameObject(model, location, index);
            }
            case "ANIM": {
                var type = AnimationType.valueOf(args[0]);
                return new AnimatedGameObject(model, type);
            }
            case "DOOR": {
                var location = Location.valueOf(args[0]);
                var id = Integer.parseInt(args[1]);

                for (var unfinished : unfinishedDoors) {
                    if (unfinished.location == location && unfinished.otherHalf == id) {
                        unfinishedDoors.remove(unfinished);
                        var lowerId = Math.min(id, unfinished.id);
                        return new DoorGameObject(model, unfinished.model, location, (int) Math.floor(lowerId / 2.0f));
                    }
                }

                var unfinished = new UnfinishedDoor();
                unfinished.location = location;
                unfinished.model = model;
                unfinished.id = id;
                unfinished.otherHalf = id % 2 == 0 ? id - 1 : id + 1;
                unfinishedDoors.add(unfinished);
                break;
            }
            case "TOOL": {
                var location = Location.valueOf(args[0]);
                var type = ToolType.valueOf(args[1]);
                return new ToolGameObject(model, location, type);
            }
        }

        return null;
    }

    private static int tryParseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
