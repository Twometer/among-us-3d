package de.twometer.amongus.net.client;

import de.twometer.amongus.core.AmongUs;

public class NetHandler {

    private final NetClient client;
    private final AmongUs amongUs;

    public NetHandler(NetClient client) {
        this.client = client;
        this.amongUs = AmongUs.get();
    }

    public void handle(Object o) {

    }

}
