package de.twometer.amongus.net.server;

public class ServerSession {

    public void join(PlayerConnection c) {
        c.session = this;
    }

    public boolean isFull() {
        return false;
    }

    public boolean isUsernameTaken(String username) {
        return false;
    }

}
