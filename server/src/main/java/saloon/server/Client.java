package saloon.server;

import java.net.InetAddress;

public record Client(InetAddress clientAddress, int clientPort, String username) {}
