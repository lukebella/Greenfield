package CleaningRobot.GRPC;

import static Parameters.RobotParameters.getHeartbeat;

public class SendHeartbeat extends Thread {

    private GRPCClient grpcClient;
    private volatile boolean stopCondition = false;
    public SendHeartbeat(GRPCClient grpcClient) {
        this.grpcClient=grpcClient;
    }

    @Override
    public void run() {
        while(!stopCondition) {
            try {
                Thread.sleep(getHeartbeat());
                grpcClient.heartbeating();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void setStopCondition(boolean stopCondition) {
        this.stopCondition = stopCondition;
    }
}
