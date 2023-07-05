package CleaningRobot.GRPC;

import CleaningRobot.GoToMechanic.Maintenance;
import CleaningRobot.Initialization.LaunchRobot;
import CleaningRobot.Initialization.Robot;
import com.example.grpc.MechanicServiceGrpc;
import com.example.grpc.Mechanic;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class MechanicServiceImpl extends MechanicServiceGrpc.MechanicServiceImplBase {

    private static List<Robot> waitingList = new ArrayList<Robot>();
    public MechanicServiceImpl() {}

    @Override
    public void mechanic(Mechanic.MechanicRequest request, StreamObserver<Mechanic.MechanicResponse> responseObserver) {
        Long timestampRequest = request.getTimestamp();
        if (Maintenance.isIntoMechanic() || (Maintenance.isRobotBroken() && Maintenance.getCurrentTimestamp()<timestampRequest)) {
            waitingList.add(new Robot(request.getID(), request.getAddress(), request.getPort()));
            System.out.println("MechanicServiceImpl::waitingList "+waitingList);
            responseObserver.onNext(Mechanic.MechanicResponse.newBuilder().setResponse("NO").build());
        }
        else {responseObserver.onNext(Mechanic.MechanicResponse.newBuilder().setResponse("OK").build());}
        responseObserver.onCompleted();
    }

    public static List<Robot> getWaitingList(){
        synchronized (waitingList) {
            return waitingList;
        }
    }


    public static void removeFromWaitingList(String ID) {
        synchronized (waitingList) {
            for (int i=0; i<waitingList.size(); i++) {
                if (waitingList.get(i).getID().equals(ID)) {
                    waitingList.remove(i);
                    break;
                }
            }
        }

    }
}

