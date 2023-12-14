package saloon.client;

import picocli.CommandLine;

@CommandLine.Command(name = "ChatClient", mixinStandardHelpOptions = true, version = "1.0",
                     description = "Client UDP pour un chat en ligne")
public class ClientLauncher {

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "Nom du client")
    private String clientName;

    @CommandLine.Option(names = {"-h", "--host"}, required = true, description = "Adresse IP du serveur")
    private String serverHost;

    @CommandLine.Option(names = {"-p", "--port"}, required = true, description = "Port du serveur")
    private int serverPort;

    public static void main(String[] args) {
        CommandLine.run(new SaloonClient(), args);
    }
}
