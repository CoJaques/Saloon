package saloon.client;

import saloon.common.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import picocli.CommandLine;

import java.util.Objects;
import java.util.Scanner;


@CommandLine.Command(name = "ChatClient", mixinStandardHelpOptions = true, version = "1.0",
        description = "Client UDP pour un chat en ligne")
public class SaloonClient implements Runnable {

    private String userName;
    private final Scanner scanner = new Scanner(System.in);
    private byte[] buffer;

    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            // Création du socket unicast
            socket = new DatagramSocket();

            // Thread pour la réception des messages du serveur
            DatagramSocket clientToServSocket = socket;
            Thread receiveThread = new Thread(() -> {
                ReceiveMessage(clientToServSocket);
            });
            receiveThread.start();

            // Thread pour l'envoi des messages au salon
            DatagramSocket clientToSaloonSocket = socket;
            Thread sendThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = scanner.nextLine();
                        Message msgType = defineMessageType(message);

                        switch (msgType) {
                            case CONNECT -> ConnectMessage(message, clientToSaloonSocket, Utils.HOST, Utils.PORT);
                            case MSG ->
                            case PM ->
                            case WHO -> WhoMessage(message, clientToSaloonSocket, Utils.HOST, Utils.PORT);
                            case QUIT -> QuitMessage(message, clientToSaloonSocket, Utils.HOST, Utils.PORT); // TODO WSI Gestion deco
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            sendThread.start();


            // Attendez que les threads se terminent
            receiveThread.join();
            sendThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // region Send / Receive
    private void sendMessageToSaloon(Message msgType, String txt, DatagramSocket socket, String serverHost, int serverPort) {
        try (DatagramSocket datagramSocket = socket) {
            String formattedMessage = Utils.formatMessage(msgType, userName, "Saloon", txt);
            byte[] messageBytes = formattedMessage.getBytes(StandardCharsets.UTF_8);
            InetAddress serverAddress = InetAddress.getByName(serverHost);
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, serverPort);
            datagramSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ReceiveMessage(DatagramSocket clientToServSocket) {
        try {
            while (true) {
                buffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                clientToServSocket.receive(receivedPacket);

                String receivedMessage = new String(
                        receivedPacket.getData(),
                        receivedPacket.getOffset(),
                        receivedPacket.getLength(),
                        StandardCharsets.UTF_8
                );
                // TODO WSI : MANAGE_ANSWER Gérer le cas du /qui
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    // endregion


    // region command
    private void ConnectMessage(String message, DatagramSocket socket, String serverHost, int serverPort) {
        userName = getUserName();
        sendMessageToSaloon(Message.CONNECT, message, socket, serverHost, serverPort);
    }

    private void WhoMessage (String message, DatagramSocket socket, String serverHost, int serverPort) {
        sendMessageToSaloon(Message.WHO, message, socket, serverHost, serverPort);
    }

    private void QuitMessage(String message, DatagramSocket socket, String serverHost, int serverPort) {
        sendMessageToSaloon(Message.QUIT, message, socket, serverHost, serverPort);
    }
    // endregion


    // Private tool
    private String getUserName() {
        do {
            System.out.println("Enter username");
            userName = scanner.nextLine();

            // Vérification si le nom d'utilisateur est en UTF-8
            if (!isUTF8(userName)) {
                System.out.println("Invalid username. Please enter a valid UTF-8 username.");
            }
        } while (!isUTF8(userName));

        return userName;
    }

    private boolean isUTF8(String str) {
        try {
            // Tentative de conversion de la chaîne en tableau de bytes en utilisant UTF-8
            str.getBytes(StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // endregion
}