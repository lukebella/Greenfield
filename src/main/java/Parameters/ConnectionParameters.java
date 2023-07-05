package Parameters;

//Class which I put the parameters in order to avoid repetitions

public class ConnectionParameters {

    //Server
    private static final String adminServerAddress = "localhost";
    private static final int serverPort = 2345;
    public static String getAdminServerAddress() {
        return adminServerAddress;
    }
    public static int getServerPort() {
        return serverPort;
    }

    //Robot
    private final static String welcome = "/robots/hello";
    private final static String addRobot = "/robots/add";
    private final static String robotList = "/robots/robotlist";
    private final static String removeRobot = "/robots/remove";
    public static String getPathWelcome() {return welcome;}
    public static String getPathAddRobot() {return addRobot;}
    public static String getPathRobotList() {return robotList;}
    public static String getRemoveRobot() {return removeRobot;}

    //MQTT
    private static final String broker = "tcp://localhost:1883";
    private static final String topic = "greenfield/pollution/district";
    private static final String nameMQTTServer = "MQTTServer";
    private static final int numDistricts = 4;
    public static String getBroker() {
        return broker;
    }
    public static String getTopic() {
        return topic;
    }
    public static String getNameMQTTServer() {return nameMQTTServer;}
    public static int getNumDistricts() {
        return numDistricts;
    }


    //Administrator Client
    private static final String avgNPollutions = "/statistics/npollutions";
    private static final String overallAvg = "/statistics/overallaverage";
    public static String getClientAvgNPollutions() { return avgNPollutions; }
    public static String getClientOverallAvg() { return overallAvg; }

}
