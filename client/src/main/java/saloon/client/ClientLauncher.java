package saloon.client;

import picocli.CommandLine;
import saloon.common.Utils;

@CommandLine.Command(name = "ChatClient", mixinStandardHelpOptions = true, version = "1.0",
                     description = "Client UDP pour un chat en ligne")
public class ClientLauncher {

    @CommandLine.Option(names = {"-h", "--host"}, required = true, description = "Adresse IP du serveur",
                        defaultValue = Utils.HOST)
    private String serverHost;

    @CommandLine.Option(names = {"-p", "--port"}, required = true, description = "Port du serveur",
                        defaultValue = "" + Utils.PORT) // Convertir Utils.PORT en string
    private int serverPort;

    public static void main(String[] args) {
        CommandLine.run(new SaloonClient(), args);
    }
}
