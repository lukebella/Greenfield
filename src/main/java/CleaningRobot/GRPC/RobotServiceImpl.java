package CleaningRobot.GRPC;

import CleaningRobot.Initialization.Robot;
import SmartCity.Cell;
import com.example.grpc.RobotServiceOuterClass;
import com.example.grpc.RobotServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

    List<Robot> robotList;

    public RobotServiceImpl(List<Robot> robotList) {
        this.robotList = robotList;
    }

    @Override
    public void hello(RobotServiceOuterClass.RobotRequest request, StreamObserver<RobotServiceOuterClass.RobotResponse> responseObserver) {
        synchronized(robotList) {
            System.out.println("[FROM CLIENT] New Robot: \n" +
                    "\tID: "+request.getID()+"\n"+
                    "\tAddress: "+request.getAddress()+"\n"+
                    "\tPort: "+request.getPort()+"\n"+
                    "\tGrid Position: "+new Cell(request.getRow(), request.getColumn())+"\n"+
                    "\tDistrict: "+request.getDistrict()+"\n"
            );
            robotList.add(new Robot(request.getID(),request.getAddress(),request.getPort()));
            System.out.println("Sending the response to the client...\n");
            responseObserver.onNext(RobotServiceOuterClass.RobotResponse.newBuilder().setResponse("Hello from '" +
                    GRPCServer.getID() + "'").build());
            responseObserver.onCompleted();
        }
    }

}

