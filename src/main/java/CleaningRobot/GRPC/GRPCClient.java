package CleaningRobot.GRPC;

import AdministratorServer.REST.StartRobotListResponse;
import CleaningRobot.GoToMechanic.Maintenance;
import CleaningRobot.Initialization.LaunchRobot;
import CleaningRobot.Initialization.Robot;
import SmartCity.Cell;
import SmartCity.District;
import com.example.grpc.*;
import com.example.grpc.CrashedServiceOuterClass.*;
import com.example.grpc.GoodbyeServiceOuterClass.*;
import com.example.grpc.Heartbeat.*;
import com.example.grpc.RobotServiceOuterClass.*;
import com.example.grpc.Mechanic.*;
import com.sun.jersey.api.client.ClientResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static Parameters.ConnectionParameters.getRemoveRobot;
public class GRPCClient {

    Robot r;
    StartRobotListResponse sr;
    Cell c;

    public GRPCClient(Robot r, StartRobotListResponse sr, Cell c) {
        this.r = r;
        this.sr = sr;
        this.c = c;
    }

    //Presenting to all other robots
    public void presenting() {
        synchronized (sr.getRobotList()) {
            for (int robot = 0; robot < sr.getRobotList().size(); robot++) {
                if (!(r.getID().equals(sr.getRobotList().get(robot).getID()))) {

                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(sr.getRobotList().get(robot).getAddress() +
                            ":" + sr.getRobotList().get(robot).getPort()).usePlaintext().build();

                    RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
                    stub.hello(RobotRequest.newBuilder().setID(r.getID())
                                    .setAddress(r.getAddress())
                                    .setPort(r.getPort())
                                    .setRow(c.rowCell)
                                    .setColumn(c.columnCell)
                                    .setDistrict(District.getDistrict(c))
                                    .build(),
                            new StreamObserver<RobotResponse>() {

                                public void onNext(RobotResponse robotResponse) {
                                    System.out.println(sr.getRobotList());
                                    System.out.println("[FROM SERVER] " + robotResponse.getResponse());
                                }

                                public void onError(Throwable throwable) {
                                }

                                public void onCompleted() {
                                    //channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    //"Quit" gently
    public void goodbye() {
        synchronized (sr.getRobotList()) {
            for (int robot = 0; robot < sr.getRobotList().size(); robot++) {
                if (!(r.getID().equals(sr.getRobotList().get(robot).getID()))) {

                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(sr.getRobotList().get(robot).getAddress() +
                            ":" + sr.getRobotList().get(robot).getPort()).usePlaintext().build();

                    GoodbyeServiceGrpc.GoodbyeServiceStub stub = GoodbyeServiceGrpc.newStub(channel);

                    int index = robot;
                    stub.goodbye(GoodbyeRequest.newBuilder().setID(r.getID())
                                    .setAddress(r.getAddress())
                                    .setPort(r.getPort()).build(),
                            new StreamObserver<GoodbyeResponse>() {
                                public void onNext(GoodbyeResponse goodbyeResponse) {
                                    System.out.println("[FROM SERVER] " + goodbyeResponse.getResponse());
                                    if (LaunchRobot.isFixRobot()) {  //TODO with Thread.JOIN I don't think is useful anymore
                                        System.out.println("Crashed robot, remove from the waiting list");
                                        MechanicServiceImpl.removeFromWaitingList(sr.getRobotList().get(index).getID());
                                        Maintenance.increaseConsensus();
                                        LaunchRobot.getMaintenance().notifyMaintenance();
                                    }
                                }

                                public void onError(Throwable throwable) {
                                }

                                public void onCompleted() {
                                    channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }

    //Check Heartbeats
    public synchronized void heartbeating() {
        //synchronized (sr.getRobotList()) {
            for (int robot = 0; robot < sr.getRobotList().size(); robot++) {
                if (!(r.getID().equals(sr.getRobotList().get(robot).getID()))) {

                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(sr.getRobotList().get(robot).getAddress() +
                            ":" + sr.getRobotList().get(robot).getPort()).usePlaintext().build();
                    int index = robot;
                    HeartbeatServiceGrpc.HeartbeatServiceStub stub = HeartbeatServiceGrpc.newStub(channel);
                    stub.heartbeat(HeartbeatRequest.newBuilder().build(),
                            new StreamObserver<HeartbeatResponse>() {
                                public void onNext(HeartbeatResponse heartbeatResponse) {
                                }

                                public void onError(Throwable throwable) {
                                    System.out.println("ON ERROR: " + sr.getRobotList().get(index) + " CRASHED");
                                    Robot robotToDelete = sr.getRobotList().get(index);
                                    remove(robotToDelete.getID());
                                    System.out.println(robotToDelete + " index: " + index);
                                    sendCrashedToAll(robotToDelete);
                                    ClientResponse crashedToServer = LaunchRobot.deleteRequest(LaunchRobot.getClient(), LaunchRobot.getServerAddress() + getRemoveRobot(), robotToDelete);
                                    System.out.println(crashedToServer.toString());
                                    if (Maintenance.isRobotBroken()) {
                                        System.out.println("Crashed robot, remove from the waiting list");
                                        MechanicServiceImpl.removeFromWaitingList(robotToDelete.getID());
                                        Maintenance.increaseConsensus();
                                        LaunchRobot.getMaintenance().notifyMaintenance();
                                    }

                                }

                                public void onCompleted() {
                                    channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        //}
    }

    //Tells to the other robots which robot is crashed
    private void sendCrashedToAll(Robot cr) {
        //synchronized (sr.getRobotList()) {
            for (int robot = 0; robot < sr.getRobotList().size(); robot++) {
                if (!(r.getID().equals(sr.getRobotList().get(robot).getID()))) {
                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(sr.getRobotList().get(robot).getAddress() +
                            ":" + sr.getRobotList().get(robot).getPort()).usePlaintext().build();

                    CrashedServiceGrpc.CrashedServiceStub stub = CrashedServiceGrpc.newStub(channel);
                    stub.crashed(CrashedRequest.newBuilder()
                                    .setID(cr.getID())
                                    .setAddress(cr.getAddress())
                                    .setPort(cr.getPort())
                                    .build(),
                            new StreamObserver<CrashedResponse>() {
                                public void onNext(CrashedResponse crashedResponse) {
                                    System.out.println("[FROM SERVER] " + crashedResponse.getResponse());
                                }

                                public void onError(Throwable throwable) {
                                }

                                public void onCompleted() {
                                    channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        //}

    }

    //Mechanic
    public void maintenance(List<Robot> partialRobotList, long whenImBroken) {
        synchronized (partialRobotList) {
            for (int robot = 0; robot < partialRobotList.size(); robot++) {
                if (!(r.getID().equals(partialRobotList.get(robot).getID()))) {
                    System.out.println("Contacting robot: " + partialRobotList.get(robot).getID());
                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(partialRobotList.get(robot).getAddress() +
                            ":" + partialRobotList.get(robot).getPort()).usePlaintext().build();
                    MechanicServiceGrpc.MechanicServiceStub stub = MechanicServiceGrpc.newStub(channel);
                    int index = robot;
                    stub.mechanic(MechanicRequest.newBuilder()
                                    .setID(r.getID())
                                    .setAddress(r.getAddress())
                                    .setPort(r.getPort())
                                    .setTimestamp(whenImBroken)
                                    .build(),
                            new StreamObserver<MechanicResponse>() {
                                public void onNext(MechanicResponse mechanicResponse) {
                                    if (mechanicResponse.getResponse().equals("NO")) {

                                    } else if (mechanicResponse.getResponse().equals("OK")) {
                                        Maintenance.increaseConsensus();
                                        LaunchRobot.getMaintenance().notifyMaintenance();
                                    }
                                    System.out.println("[FROM SERVER " + partialRobotList.get(index).getID() + "] " + mechanicResponse.getResponse());
                                }

                                public void onError(Throwable throwable) {
                                    //In case one robot crashes, the list spot of this robot is liberated
                                    Maintenance.increaseConsensus();
                                    LaunchRobot.getMaintenance().notifyMaintenance();
                                }

                                public void onCompleted() {
                                    channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }

    public void maintenanceOver() {
        synchronized (MechanicServiceImpl.getWaitingList()) {
            for (int robot = 0; robot < MechanicServiceImpl.getWaitingList().size(); robot++) {
                System.out.println(MechanicServiceImpl.getWaitingList().get(robot));
                if (!(r.getID().equals(MechanicServiceImpl.getWaitingList().get(robot).getID()))) {

                    final ManagedChannel channel = ManagedChannelBuilder.forTarget(MechanicServiceImpl.getWaitingList().get(robot).getAddress() +
                            ":" + MechanicServiceImpl.getWaitingList().get(robot).getPort()).usePlaintext().build();
                    MaintenanceOverServiceGrpc.MaintenanceOverServiceStub stub = MaintenanceOverServiceGrpc.newStub(channel);
                    int index = robot;
                    stub.maintenanceOver(MaintenanceOver.MaintenanceOverRequest.newBuilder()
                                    .setOk("OK")
                                    .build(),
                            new StreamObserver<MaintenanceOver.MaintenanceOverResponse>() {
                                public void onNext(MaintenanceOver.MaintenanceOverResponse maintenanceOverResponse) {
                                    System.out.println("Maintenance Over: Robot " + MechanicServiceImpl.getWaitingList().get(index).getID() + " complete fixing");
                                    System.out.println("[FROM SERVER] " + maintenanceOverResponse.getReceived());
                                }

                                public void onError(Throwable throwable) {

                                }

                                public void onCompleted() {
                                    channel.shutdown();
                                }
                            });
                    try {
                        channel.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }


    }

    public void remove(String ID) {
        synchronized (sr.getRobotList()) {
            for (int i = 0; i < sr.getRobotList().size(); i++) {
                if (sr.getRobotList().get(i).getID().equals(ID)) {
                    sr.getRobotList().remove(i);
                    break;
                }
            }
        }

    }

}


