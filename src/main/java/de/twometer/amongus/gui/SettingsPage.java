package de.twometer.amongus.gui;

import com.google.gson.Gson;
import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.util.UserSettings;

public class SettingsPage extends BasePage {

    private final Gson gson = new Gson();

    public SettingsPage() {
        super("Settings.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.call("set", AmongUs.get().getUserSettings());
    }

    public void apply(String settingsJson) {
        var settings = gson.fromJson(settingsJson, UserSettings.class);
        AmongUs.get().getUserSettings().setGraphics(settings);
        AmongUs.get().getUserSettings().save();
        AmongUs.get().reloadFxConfig();
        goBack();
    }

    public void back() {
        goBack();
    }

}
