package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.util.UserSettings;

public class SettingsPage extends BasePage {

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
        AmongUs.get().getUserSettings().copyFromJs(settings);
        AmongUs.get().getUserSettings().save();
        AmongUs.get().reloadConfig();
        goBack();
    }

    public void back() {
        goBack();
    }

}
