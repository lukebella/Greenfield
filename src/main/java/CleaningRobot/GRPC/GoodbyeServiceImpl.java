package CleaningRobot.GRPC;

import CleaningRobot.Initialization.Robot;
import com.example.grpc.GoodbyeServiceGrpc;
import com.example.grpc.GoodbyeServiceOuterClass;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class GoodbyeServiceImpl extends GoodbyeServiceGrpc.GoodbyeServiceImplBase {
    List<Robot> robotList;

    public GoodbyeServiceImpl(List<Robot> robotList) {
        this.robotList = robotList;
    }

    @Override
    public void goodbye(GoodbyeServiceOuterClass.GoodbyeRequest request, StreamObserver<GoodbyeServiceOuterClass.GoodbyeResponse> responseObserver) {
        String clientStringRequest = request.getID();
        System.out.println("[FROM CLIENT] Remove Robot: " + clientStringRequest);
        removeRobot(new Robot(request.getID(),request.getAddress(),request.getPort()), robotList);
        // sending the response to the client
        System.out.println("Sending the response to the client...\n");
        responseObserver.onNext(GoodbyeServiceOuterClass.GoodbyeResponse.newBuilder().setResponse("Bye from '" +
                GRPCServer.getID() + "'").build());
        responseObserver.onCompleted();
    }

    public void removeRobot(Robot r, List<Robot> listRobot) {
        synchronized (listRobot) {
            for (int i=0; i<listRobot.size(); i++) {
                if(listRobot.get(i).getID().equals(r.getID())) {
                    this.robotList.remove(i);
                    break;
                }
            }
        }
    }

}

