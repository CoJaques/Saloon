package saloon.common;

public class Utils {
    public final static int UNICAST_PORT = 1312;
    public final static int MULTICAST_PORT = UNICAST_PORT + 1;
    public final static String DEFAULT_HOST = "192.168.23.23";
    public final static String DEFAULT_MULTICAST = "239.1.1.1";
    public final static String EOL = "\n";
    public final static String SEPARATOR = ";";

    public static String formatMessage(Message msg, String sourceUserName, String destUserName, String txt) {
        return msg.name() + Utils.SEPARATOR + sourceUserName + Utils.SEPARATOR
                + destUserName + Utils.SEPARATOR + txt + Utils.EOL;
    }
}
