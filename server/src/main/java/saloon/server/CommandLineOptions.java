package saloon.server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import saloon.common.Utils;

@Command(name = "Server", mixinStandardHelpOptions = true, version = "1.0")
public class CommandLineOptions {

    @Option(names = {"-MP", "--MultiPort"}, description = "The multicast port of the server")
    private int mPort = Utils.MULTICAST_PORT;

    @Option(names = {"-UP", "--UniPort"}, description = "The unicast port of the server")
    private int uPort = Utils.UNICAST_PORT;

    @Option(names = {"-M", "--MultiCastAdress"}, description = "The multicast address of the server")
    private String multicastAddress = Utils.DEFAULT_MULTICAST;

    @Option(names = {"-A", "--Adapter"}, description = "Define the adapter to use for the multicast")
    private String adapter = null;

    public int getMultiPort() {
        return mPort;
    }

    public int getUniPort() {
        return uPort;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public String getAdapter() {
        return adapter;
    }
}
