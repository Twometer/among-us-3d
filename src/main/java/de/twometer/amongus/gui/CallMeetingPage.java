package de.twometer.amongus.gui;

import de.twometer.neko.util.Log;

public class CallMeetingPage extends BasePage {

    public CallMeetingPage() {
        super("CallMeeting.html");
    }

    public void callEmergency() {
        Log.i("Emergency Meeting!");
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
