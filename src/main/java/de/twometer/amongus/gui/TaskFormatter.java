package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.PlayerTask;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.model.TaskType;

public class TaskFormatter {

    public static String formatSabotage(Sabotage sabotage, int dur) {
        return String.format(i18n("sabotage." + sabotage.name()), dur);
    }

    public static String format(PlayerTask task) {
        var stage = task.getNextStage();

        var sb = new StringBuilder();
        sb.append(formatLocation(stage.getLocation()));
        sb.append(": ");
        sb.append(formatStage(task.getProgress(), stage.getTaskType(), task));

        if (task.isMultiStage()) {
            sb.append(" (").append(task.getProgress()).append("/").append(task.length()).append(")");
        } else if (task.isTimerRunning()) {
            var remain = Math.round((task.getEndTime() - System.currentTimeMillis()) / 1000.0);
            sb.append(" (").append(remain).append(")");
        }

        return sb.toString();
    }

    public static String formatLocation(Location location) {
        return i18n("rooms." + location.name());
    }

    private static String formatStage(int progress, TaskType type, PlayerTask task) {
        switch (type) {
            case DivertPower:
                var dst = formatLocation(task.getLastStage().getLocation());
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
