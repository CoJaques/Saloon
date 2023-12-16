package saloon.server;

import saloon.common.Message;
import saloon.common.Utils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SaloonServer {
    private final Map<String, Client> clients = new HashMap<>();
    private final MulticastSocket multicastSocket;
    private final DatagramSocket serverSocket;
    private final InetSocketAddress group;

    SaloonServer(int multiPort, int uniPort, String mutlicastAddress, String interfaceName) throws IOException {
        InetAddress resolvedAddress = InetAddress.getByName(mutlicastAddress);
        group = new InetSocketAddress(resolvedAddress, multiPort);
        this.multicastSocket = new MulticastSocket(multiPort);
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        multicastSocket.joinGroup(group, networkInterface);

        serverSocket = new DatagramSocket(uniPort);
    }

    public void startServer() {
        System.out.println("Saloon Server is starting...");

        System.out.println("Saloon Server is running...");

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
            try {
                serverSocket.receive(incomingPacket);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO manage exception
            }
            managePacket(incomingPacket);
        }

    }

    private Message parseCommandAndCheckLenght(String[] parsedMessage) {
        if (parsedMessage.length < 2) {
            System.out.println("Invalid message, username or command not define " + Arrays.toString(parsedMessage));
            return null;
        }
        return getCommand(parsedMessage);
    }

    public void managePacket(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());

        System.out.println("Received message: " + message);

        String[] messageParts = message.split(Utils.SEPARATOR);

        Message command = parseCommandAndCheckLenght(messageParts);
        if (command == null) return;

        Client client = new Client(packet.getAddress(), packet.getPort(), messageParts[1]);
        manageCommand(command, client, messageParts);
    }

    private void manageConnection(Client client) {
        if (!clients.containsKey(client.username())) {
            // TODO Add check for username duplicate
            if (clients.isEmpty()) {
                sendMulticastMessage(client, "Welcome to the saloon");
            } else {
                sendMulticastMessage(client, "New client " + client.username() + " joined the saloon");
            }

            clients.put(client.username(), client);
            sendUnicastMessage(Message.OK.name(), client);
            System.out.println("Client " + client.username() + " connected");
        } else {
            System.out.println("Client " + client.username() + " already connected");
            sendUnicastMessage(Message.KO.name(), client);
        }
    }

    private void manageCommand(Message command, Client client, String[] messageParts) {

        if (command == Message.CONNECT) {
            manageConnection(client);
        } else if (clients.containsKey(client.username())) {
            switch (command) {
                case MSG:
                    sendMulticastMessage(client, messageParts[2]);
                    break;
                case PM:
                    formatAndSendPrivateMessage(client, messageParts);
                    break;
                case WHO:
                    senWhoRequest(client);
                    break;
                case QUIT:
                    manageQuitRequest(client);
                    break;
                default:
                    System.out.println("Invalid command " + command);
            }
        } else {
            System.out.println("Unregistered client tried to send a message : " + messageParts[0]);
        }
    }

    private void manageQuitRequest(Client client) {
        clients.remove(client.username());
        sendMulticastMessage(client, "Client " + client.username() + " left the saloon");
    }

    private void senWhoRequest(Client client) {
        StringBuilder sb = new StringBuilder();
        for (Client c : clients.values()) {
            sb.append(c.username()).append(Utils.SEPARATOR);
        }
        sendUnicastMessage(Message.WHO.name() + Utils.SEPARATOR + sb.toString() + Utils.EOL, client);
    }

    private void formatAndSendPrivateMessage(Client sender, String[] messageParts) {
        String message =
                Message.PM.name() + Utils.SEPARATOR + sender.username() + Utils.SEPARATOR + messageParts[2] + Utils.SEPARATOR + messageParts[3] + Utils.EOL;

        Client receiver = clients.get(messageParts[2]);
        if (receiver == null) {
            System.out.println("Client " + messageParts[2] + " not found");
            sendUnicastMessage(Message.PM.name() + Utils.SEPARATOR + "Client " + messageParts[1] + " not found" + Utils.EOL, sender);
            return;
        }
        sendUnicastMessage(message, receiver);
    }

    private void sendMulticastMessage(Client sender, String content) {
        try {
            String message =
                    Message.MSG.name() + Utils.SEPARATOR + sender.username() + Utils.SEPARATOR + content + Utils.SEPARATOR + Utils.EOL;
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group);
            multicastSocket.send(packet);
        } catch (Exception e) {
            System.out.println("Multicast exception: " + e);
        }
    }

    private void sendUnicastMessage(String message, Client receiver) {
        try {
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiver.clientAddress(), receiver.clientPort());
            serverSocket.send(packet);
        } catch (Exception e) {
            System.out.println("Multicast exception: " + e);
        }
    }

    private static Message getCommand(String[] messageParts) {
        Message msg;
        try {
            msg = Message.valueOf(messageParts[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid command " + Arrays.toString(messageParts));
            return null;
        }
        return msg;
    }
}
