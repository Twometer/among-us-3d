package de.twometer.amongus.net.server;

public interface PacketHandler<T> {

    void handle(PlayerConnection connection, T message);

}
