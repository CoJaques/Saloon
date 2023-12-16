package saloon.client;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import saloon.common.Utils;

@Command(name = "Client", mixinStandardHelpOptions = true, version = "1.0")
public class CommandLineOptions {

    @Option(names = {"-UP", "--UniPort"}, description = "The unicast port of the server")
    private int hostPort = Utils.UNICAST_PORT;

    @Option(names = {"-MP", "--MultiPort"}, description = "The multicast port of the server")
    private int mPort = Utils.MULTICAST_PORT;

    @Option(names = {"-H", "--DefaultHost"}, description = "The server host")
    private String host = Utils.DEFAULT_HOST;

    @Option(names = {"-M", "--MultiCastAdress"}, description = "The multicast address of the server")
    private String multicastAddress = Utils.DEFAULT_MULTICAST;

    @Option(names = {"-A", "--Adapter"}, description = "Define the adapter to use for the multicast")
    private String adapter = null;

    public int getHostPort() {
        return hostPort;
    }

    public int getMultiPort() { return mPort; }

    public String getHost() {
        return host;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public String getAdapter() {
        return adapter;
    }
}
