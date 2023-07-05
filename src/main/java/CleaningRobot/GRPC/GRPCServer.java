package CleaningRobot.GRPC;

import CleaningRobot.Initialization.Robot;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.List;

public class GRPCServer {
    private int port;
    private static String ID;
    private List<Robot> robotList;
    private io.grpc.Server server;

    public GRPCServer(String ID, int port, List<Robot> robotList) {
        GRPCServer.ID = ID;
        this.port = port;
        this.robotList = robotList;
    }
    public void startServer() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new RobotServiceImpl(robotList))
                    .addService(new GoodbyeServiceImpl(robotList))
                    .addService(new HeartbeatServiceImpl(robotList))
                    .addService(new CrashedServiceImpl(robotList))
                    .addService(new MechanicServiceImpl())
                    .addService(new MaintenanceOverServiceImpl())
                    .build();

            System.out.println("GRPC-Server started!\n");
            server.start();
            //server.awaitTermination();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getID() {
        return ID;
    }

    public int getPort() {
        return port;
    }

    public void shutdown() {
        try {
            server.shutdown();
            server.awaitTermination();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
