package CleaningRobot.GRPC;

import CleaningRobot.Initialization.Robot;
import com.example.grpc.CrashedServiceGrpc;
import com.example.grpc.CrashedServiceOuterClass;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class CrashedServiceImpl extends CrashedServiceGrpc.CrashedServiceImplBase {

    List<Robot> robotList;

    public CrashedServiceImpl(List<Robot> robotList) {
        this.robotList = robotList;
    }

    @Override
    public void crashed(CrashedServiceOuterClass.CrashedRequest request, StreamObserver<CrashedServiceOuterClass.CrashedResponse> responseObserver) {
            String clientStringRequest = request.getID();
            System.out.println("[FROM CLIENT] Robot: " + clientStringRequest+ " crashed!!!");
            removeRobot(new Robot(request.getID(),request.getAddress(),request.getPort()),robotList);
            System.out.println("Sending the response to the client...\n");
            responseObserver.onNext(CrashedServiceOuterClass.CrashedResponse.newBuilder().setResponse(GRPCServer.getID()+": removed from my list").build());
            responseObserver.onCompleted();
    }

    private void removeRobot(Robot r, List<Robot> listRobot) {
        synchronized (robotList) {
            for (int i = 0; i < listRobot.size(); i++) {
                if (listRobot.get(i).getID().equals(r.getID())) {
                    this.robotList.remove(i);
                    break;
                }
            }
        }
    }

}

