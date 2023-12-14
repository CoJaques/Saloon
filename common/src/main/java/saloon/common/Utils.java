package saloon.common;

public class Utils {
    public final static int PORT = 1312;
    public final static String HOST = "192.168.23.23";
    public final static String EOL = "\n";
    public final static String SEPARATOR = ":";

    private static String formatMessage(Message msg, String sourceUserName, String destUserName, String txt) {
        return msg.name() + Utils.SEPARATOR + sourceUserName + Utils.SEPARATOR
                + destUserName + Utils.SEPARATOR + txt + Utils.EOL;
    }
}
