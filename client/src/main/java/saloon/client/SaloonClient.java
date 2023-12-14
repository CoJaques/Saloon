package saloon.client;

import saloon.common.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import picocli.CommandLine;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@CommandLine.Command(name = "ChatClient", mixinStandardHelpOptions = true, version = "1.0",
                     description = "Client UDP pour un chat en ligne")
public class SaloonClient implements Runnable {

    private final BlockingQueue<String> privateMessagesQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            // Création du socket unicast
            socket = new DatagramSocket();

            // On demande et récupère le user name
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter username");

            String userName = scanner.nextLine();
            System.out.println("Username is: " + userName);

            // Envoi du nom du client au serveur pour l'enregistrement
            sendMessage(userName, socket, Utils.HOST, Utils.PORT);

            // Thread pour la réception des messages du serveur
            DatagramSocket clientToServSocket = socket;
            Thread receiveThread = new Thread(() -> {
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
                        System.out.println("Unicast receiver (" + userName + ") received message: " + receivedMessage);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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

    private static void sendMessage(String message, DatagramSocket socket, String serverHost, int serverPort) {
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

    // TODO WSI : Méthode receive case ->
}


class CommandHandler implements Runnable {

    @Override
    public void run() {
        try {
            if (privateMessage) {
                // Logique pour envoyer un message privé
                String privateMessage = "[" + target + " (privé)]: " + String.join(" ", message);
                privateMessagesQueue.offer(privateMessage); // Ajouter à la file d'attente
            }
            else {
                System.out.println("Commande non reconnue. Utilisez --private pour envoyer un message privé.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}