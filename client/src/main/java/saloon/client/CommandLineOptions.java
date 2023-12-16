package saloon.client;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import saloon.common.Utils;

@Command(name = "Client", mixinStandardHelpOptions = true, version = "1.0")
public class CommandLineOptions {

    @Option(names = {"-UP", "--UniPort"}, description = "The unicast port of the server")
    private int uPort = Utils.UNICAST_PORT;

    @Option(names = {"-DH", "--DefaultHost"}, description = "The server host")
    private String defaultHost = Utils.DEFAULT_HOST;

    @Option(names = {"-A", "--Adapter"}, description = "Define the adapter to use for the multicast")
    private String adapter = null;

    public int getUniPort() {
        return uPort;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public String getAdapter() {
        return adapter;
    }
}
