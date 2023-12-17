package saloon.client;

import saloon.common.Message;
import saloon.common.Utils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class SaloonClient implements Runnable {

    // region ctor
    SaloonClient(String hostName, String multiHost, int serverPort, int multiPort, String interfaceName) throws IOException {
        socket = new DatagramSocket();
        this.hostName = hostName;
        this.hostPort = serverPort;

        multicastSocket = new MulticastSocket(multiPort);
        InetAddress resolvedAddress = InetAddress.getByName(multiHost);
        group = new InetSocketAddress(resolvedAddress, multiPort);
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        multicastSocket.joinGroup(group, networkInterface);
    }
    // endregion

    // region Private field
    private String userName = null;
    private final Scanner scanner = new Scanner(System.in);
    private final DatagramSocket socket;
    private final MulticastSocket multicastSocket;
    private final InetSocketAddress group;
    private final String hostName;
    private final int hostPort;
    private Thread receiveThread;
    private Thread receiveMultiThread;
    private Thread sendThread;
    // endregion

    // region Public method
    @Override
    public void run() {
        System.out.println("Saloon Client is running...");
        System.out.println("Use /connect <'usernam'> for join the Saloon !");

        try {
            // Thread pour la réception des messages du serveur
            DatagramSocket clientFromServSocket = socket;
            receiveThread = new Thread(() -> {
                listen(clientFromServSocket);
            });
            receiveThread.start();

            // Thread pour la réception des messages multicast du serveur
            MulticastSocket clientFromServMultiSocket = multicastSocket;
            receiveMultiThread = new Thread(() -> {
                listen(clientFromServMultiSocket);
            });
            receiveMultiThread.start();

            // Thread pour l'envoi des messages au salon
            DatagramSocket clientToSaloonSocket = socket;
            sendThread = new Thread(() -> {
                chat(clientToSaloonSocket);
            });
            sendThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
    // endregion

    // region Send / Receive
    private void sendMessage(Message msgType, String dest, String txt, DatagramSocket socket, String serverHost, int serverPort) {
        try {
            if (dest == null || dest.isEmpty()) {
                dest = "Saloon";
            }

            txt = formatText(txt, msgType);
            String formattedMessage = Utils.formatMessage(msgType, userName, dest, txt);
            byte[] messageBytes = formattedMessage.getBytes(StandardCharsets.UTF_8);
            InetAddress serverAddress = InetAddress.getByName(serverHost);
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen(DatagramSocket clientToServSocket) {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                clientToServSocket.receive(receivedPacket);

                String receivedMessage = new String(
                        receivedPacket.getData(),
                        receivedPacket.getOffset(),
                        receivedPacket.getLength(),
                        StandardCharsets.UTF_8
                );
                manageAnswer(receivedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageAnswer(String msg) {
        String[] chunks = msg.split(Utils.SEPARATOR);
        String messageType = chunks[0];

        if (Objects.equals(messageType, Message.KO.name())) {
            System.out.println("Username already existing !");
            userName = null;
        } else if (Objects.equals(messageType, Message.WHO.name())) {
            LinkedList<String> usersConnected = new LinkedList<>();
            for (int i = 1; i < chunks.length; ++i) {
                if (!Objects.equals(chunks[i], userName)) {
                    usersConnected.add(chunks[i]);
                }
            }

            System.out.println("Users connected :");
            for (String user : usersConnected) {
                System.out.println(user);
            }
            usersConnected.clear();
        } else if (Objects.equals(messageType, Message.MSG.name()) && !Objects.equals(chunks[1], userName)) {
            String sender = chunks[1];
            String message = chunks[2];

            System.out.println(sender + " : " + message);
        } else if (Objects.equals(messageType, Message.PM.name())) {
            String sender = chunks[1];
            String message = chunks[2];

            System.out.println(sender + " (private) : " + message);
        }
    }
    // endregion

    // region Commands
    private void connectMessage(String message, DatagramSocket socket) {
        try {
            if (userName != null) {
                System.out.println("Client '" + userName + "' is already connected !");
            } else {
                String[] chunks = message.split(" ");
                userName = chunks[1];
                sendMessage(Message.CONNECT, null, message, socket, hostName, hostPort);
            }
        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong format !");
        }
    }

    private void sendPublicMessage(String message, DatagramSocket socket) {
        if (userName == null) {
            System.out.println("You must be connected on the Saloon before sending a message !");
        } else {
            sendMessage(Message.MSG, null, message, socket, hostName, hostPort);
        }
    }

    private void sendPrivateMessage(String message, String dest, DatagramSocket socket) {
        if (userName == null) {
            System.out.println("You must be connected on the Saloon before sending a private message !");
        } else {
            sendMessage(Message.PM, dest, message, socket, hostName, hostPort);
        }
    }

    private void whoMessage(String message, DatagramSocket socket) {
        if (userName == null) {
            System.out.println("You must be connected on the Saloon before ask for users!");
        } else {
            sendMessage(Message.WHO, null, message, socket, hostName, hostPort);
        }
    }

    private void quitMessage(String message, DatagramSocket socket) {
        sendMessage(Message.QUIT, null, message, socket, hostName, hostPort);
        dispose();
        System.exit(0);
    }

    private void chat(DatagramSocket clientToSaloonSocket) {
        try {
            while (true) {
                String message = scanner.nextLine();
                Message msgType = defineMessageType(message);

                String[] chunks;
                switch (msgType) {
                    case CONNECT:
                        connectMessage(message, clientToSaloonSocket);
                        break;
                    case MSG:
                        sendPublicMessage(message, clientToSaloonSocket);
                        break;
                    case PM:
                        chunks = message.split(" ");
                        String dest = chunks[1];
                        sendPrivateMessage(message, dest, clientToSaloonSocket);
                        break;
                    case WHO:
                        whoMessage(message, clientToSaloonSocket);
                        break;
                    case QUIT:
                        quitMessage(message, clientToSaloonSocket);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // endregion

    //region Utils tool
    private Message defineMessageType(String txt) {
        if (txt.startsWith("/")) {
            String[] splittedTxt = txt.split(" ");

            if (Objects.equals(splittedTxt[0], "/connect")) {
                return Message.CONNECT;
            } else if (Objects.equals(splittedTxt[0], "/who")) {
                return Message.WHO;
            } else if (Objects.equals(splittedTxt[0], "/quit")) {
                return Message.QUIT;
            } else if (Objects.equals(splittedTxt[0], "/pm")) {
                return Message.PM;
            }
        }
        return Message.MSG;
    }

    private String formatText(String txt, Message typeMsg) {
        String[] chunks = txt.split(" ");
        StringBuilder formattedText = new StringBuilder();
        switch (typeMsg) {
            case CONNECT:
                formattedText = new StringBuilder(chunks[1]); // 0: /connect 1: <'username'> <1: ignored
                break;

            case MSG:
                formattedText = new StringBuilder(txt);
                break;

            case PM:
                for (int i = 2; i < chunks.length; ++i) {
                    formattedText.append(chunks[i]);   // 0: /pm 1: <'dest user'> <1: msg to send

                    // Ajoute un espace uniquement si ce n'est pas le dernier élément
                    if (i < chunks.length - 1) {
                        formattedText.append(" ");
                    }
                }
                break;

            case WHO, QUIT: // no need to send a txt
                break;
        }
        return formattedText.toString();
    }

    private void dispose() {
        try {
            // Fermeture des sockets
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (multicastSocket != null && !multicastSocket.isClosed()) {
                multicastSocket.close();
            }

            // Join des threads
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join();
            }
            if (receiveMultiThread != null && receiveMultiThread.isAlive()) {
                receiveMultiThread.join();
            }
            if (sendThread != null && sendThread.isAlive()) {
                sendThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(); // Traitez les exceptions de manière appropriée
        }
    }
    // endregion
}