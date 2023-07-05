package CleaningRobot.GRPC;

import CleaningRobot.Initialization.Robot;
import com.example.grpc.Heartbeat;
import com.example.grpc.HeartbeatServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class HeartbeatServiceImpl extends HeartbeatServiceGrpc.HeartbeatServiceImplBase {
    List<Robot> robotList;

    public HeartbeatServiceImpl(List<Robot> robotList) {
        this.robotList = robotList;
    }

    @Override
    public void heartbeat(Heartbeat.HeartbeatRequest request, StreamObserver<Heartbeat.HeartbeatResponse> responseObserver) {
        responseObserver.onCompleted();
    }

}
