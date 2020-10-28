package de.twometer.amongus.gui;

import de.twometer.neko.gui.Page;
import de.twometer.neko.util.Log;

public class CallMeetingPage extends Page {

    public CallMeetingPage() {
        super("CallMeeting.html");
    }

    public void callEmergency() {
        Log.i("Emergency Meeting!");
    }

}
