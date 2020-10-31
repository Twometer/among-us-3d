package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.util.Log;

public class CallMeetingPage extends BasePage {

    public CallMeetingPage() {
        super("CallMeeting.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        context.call("setCallsLeft", AmongUs.get().getSession().getMyself().emergencyMeetings);
    }

    public void callEmergency() {
        Log.i("Emergency Meeting!");
        AmongUs.get().getClient().sendMessage(new NetMessage.CallEmergency(NetMessage.EmergencyCause.Button));
        AmongUs.get().getSession().getMyself().emergencyMeetings--;
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
