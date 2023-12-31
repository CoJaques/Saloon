package saloon.client;

import picocli.CommandLine;
import java.net.SocketException;

public class ClientLauncher {

    public static void main(String[] args) throws SocketException {
        CommandLineOptions cmdOptions = new CommandLineOptions();
        new CommandLine(cmdOptions).parseArgs(args);

        try {
            SaloonClient client = new SaloonClient(cmdOptions.getHost(),
                                                   cmdOptions.getMulticastAddress(),
                                                   cmdOptions.getHostPort(),
                                                   cmdOptions.getMultiPort(),
                                                   cmdOptions.getAdapter());
            client.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}