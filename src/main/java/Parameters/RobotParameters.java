package Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RobotParameters {

    private static final int maxRobot = 100;
    private static List<Integer> usedPorts = new ArrayList<Integer>();
    private static final int heartbeat =3000;
    private static final int mechanic = 10000;
    private static final int maintenance = 10000;

    public static int getMaxRobot() {
        return maxRobot;
    }
    public static int getHeartbeat() {return heartbeat;}
    public static int getMechanic() {return mechanic;}
    public static int getMaintenance() {return maintenance;}

    public static int myPort() {
        synchronized (usedPorts) {
            int newPort = 4000 + new Random().nextInt(4000);
            if (usedPorts.contains(newPort)) {
                return myPort();
            }
            else {
                usedPorts.add(newPort);
                return newPort;
            }
        }

    }

}
