package saloon.server;

import picocli.CommandLine;

public class ServerLauncher {
    public static void main(String[] args) {
        CommandLineOptions cmdOptions = new CommandLineOptions();
        new CommandLine(cmdOptions).parseArgs(args);

        try {
            SaloonServer server = new SaloonServer(cmdOptions.getMultiPort(), cmdOptions.getUniPort(), cmdOptions.getMulticastAddress(),
                    cmdOptions.getAdapter());
            server.startServer();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
