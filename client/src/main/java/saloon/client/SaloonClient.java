package saloon.client;

import saloon.common.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import picocli.CommandLine;

import java.util.Scanner;


@CommandLine.Command(name = "ChatClient", mixinStandardHelpOptions = true, version = "1.0",
                     description = "Client UDP pour un chat en ligne")
public class SaloonClient implements Runnable {

    private String userName;
    private Scanner scanner;
    private byte[] buffer;


    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            // Création du socket unicast
            socket = new DatagramSocket();

            // Envoi du nom du client au serveur pour l'enregistrement
            ConnectMessage(socket, Utils.HOST, Utils.PORT);

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
                            sendMessage(message, clientToSaloonSocket, Utils.HOST, Utils.PORT);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            sendThread.start();


            // Attendez que les threads se terminent
            receiveThread.join();
            sendThread.join();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // region Send / Receive
    private void sendMessage(String message, DatagramSocket socket, String serverHost, int serverPort) {
        try (DatagramSocket datagramSocket = socket) {
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            InetAddress serverAddress = InetAddress.getByName(serverHost);
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, serverPort);
            datagramSocket.send(packet);
        }
        catch (Exception e) {
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
                //System.out.println("Unicast receiver (" + userName + ") received message: " + receivedMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    // endregion


    // region command
    private void ConnectMessage(DatagramSocket socket, String serverHost, int serverPort) {
        String userName = getUserName();
        String msg = "My name is " + userName;
        String formatedMsg = Utils.formatMessage(Message.CONNECT, userName, "Saloon", msg);

        sendMessage(formatedMsg, socket, serverHost, serverPort);
    }
    // endregion


    // Private tool
    private String getUserName() {
        Scanner scanner = new Scanner(System.in);
        String userName;

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


//class CommandHandler implements Runnable {
//
//    @Override
//    public void run() {
//        try {
//            if (privateMessage) {
//                // Logique pour envoyer un message privé
//                String privateMessage = "[" + target + " (privé)]: " + String.join(" ", message);
//                privateMessagesQueue.offer(privateMessage); // Ajouter à la file d'attente
//            }
//            else {
//                System.out.println("Commande non reconnue. Utilisez --private pour envoyer un message privé.");
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}