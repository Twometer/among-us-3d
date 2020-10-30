package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.TaskType;

public class TaskFormatter {

    public static String format(PlayerTask task) {
        var stage = task.getNextStage();

        var sb = new StringBuilder();
        sb.append(stage.getLocation().name());
        sb.append(": ");
        sb.append(formatStage(task.getProgress(), stage.getTaskType(), task));

        if (task.isMultiStage()) {
            sb.append(" (").append(task.getProgress()).append("/").append(task.length()).append(")");
        }

        return sb.toString();
    }

    private static String formatLocation(String location) {
        return i18n("rooms." + location);
    }

    private static String formatStage(int progress, TaskType type, PlayerTask task) {
        switch (type) {
            case DivertPower:
                var dst = formatLocation(task.getLastStage().getLocation().name());
                return String.format(i18n("tasks.DivertPower"), dst);
            case DataTransfer:
                if (progress == 0) return i18n("tasks.Download");
                else return i18n("tasks.Upload");
            default:
                return i18n("tasks." + type);
        }
    }

    private static String i18n(String key) {
        return AmongUs.get().getI18n().resolve("{" + key + "}");
    }

}
