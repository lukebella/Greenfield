package CleaningRobot.GoToMechanic;

import CleaningRobot.GRPC.GRPCClient;
import CleaningRobot.GRPC.MechanicServiceImpl;
import CleaningRobot.Initialization.LaunchRobot;
import CleaningRobot.Initialization.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Parameters.RobotParameters.getMaintenance;
import static Parameters.RobotParameters.getMechanic;

public class Maintenance extends Thread {

    private GRPCClient grpcClient;
    private List<Robot> currentRobotList;
    private static boolean robotBroken = false;
    private static int consensus;
    private static boolean intoMechanic=false;
    private static long currentTimestamp =0;
    private static volatile boolean stopCondition=false;

    private static final Object lockConsensus;
    private static final Object lockRobotBroken;
    private static final Object lockIntoMechanic;
    //private static final Object lockStopCondition;

    static {
        lockConsensus = new Object();
        lockRobotBroken = new Object();
        lockIntoMechanic = new Object();
        //lockStopCondition= new Object();
    }

    public Maintenance(GRPCClient grpcClient, List<Robot> currentRobotList) {
        this.grpcClient=grpcClient;
        this.currentRobotList=currentRobotList;
    }

    @Override
    public void run() {
        while(!stopCondition) {
            try {
                Thread.sleep(getMechanic());
                System.out.println("Trying to enter to the mechanic...");
                if ((new Random().nextInt(10) == 9) || LaunchRobot.isFixRobot()) {  //10% chance to be subject of malfunctions
                    System.out.println("Robot Broken");
                    //make GRPC
                    setRobotBroken(true);
                    consensus=0;
                    List<Robot> partialRobotList = new ArrayList<Robot>(currentRobotList);
                    currentTimestamp=System.currentTimeMillis();
                    grpcClient.maintenance(partialRobotList, currentTimestamp);
                    while(true) {
                        if (consensus==partialRobotList.size()-1) {
                            intoMechanic=true;
                            System.out.println("Mechanic...");
                            Thread.sleep(getMaintenance());  //Mechanic
                            System.out.println("Stop Mechanic...");
                            intoMechanic=false;
                            LaunchRobot.setFixRobot(false);
                            setRobotBroken(false);
                            grpcClient.maintenanceOver();
                            MechanicServiceImpl.getWaitingList().clear();

                            break;
                        }
                        else {
                            System.out.println("Waiting mechanic...");
                            synchronized (this){wait();}}
                    }

                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isRobotBroken() {
        synchronized (lockRobotBroken) {
            return robotBroken;
        }

    }

    public void setRobotBroken(boolean b) {
        synchronized (lockRobotBroken) {
            robotBroken=b;
        }
    }

    public static int getConsensus() {
        synchronized(lockConsensus) {
            return consensus;
        }
    }

    public static void increaseConsensus() {
        synchronized(lockConsensus) {
            Maintenance.consensus ++;
        }

    }

    public static boolean isIntoMechanic() {
        synchronized (lockIntoMechanic) {
            return intoMechanic;
        }

    }

    public static long getCurrentTimestamp() {
        return currentTimestamp;
    }

    public synchronized void notifyMaintenance() { notify();}

    public boolean isStopCondition() {
        //synchronized (lockStopCondition) {
            return stopCondition;
        //}

    }

    public static void setStopCondition(boolean stopCondition) {
        //synchronized (lockStopCondition) {
            Maintenance.stopCondition = stopCondition;
        //}
    }

}
