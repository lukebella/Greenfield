package CleaningRobot.GRPC;

import CleaningRobot.GoToMechanic.Maintenance;
import CleaningRobot.Initialization.LaunchRobot;
import com.example.grpc.MaintenanceOver;
import com.example.grpc.MaintenanceOverServiceGrpc;
import io.grpc.stub.StreamObserver;


public class MaintenanceOverServiceImpl extends MaintenanceOverServiceGrpc.MaintenanceOverServiceImplBase {

    public MaintenanceOverServiceImpl() {}

    @Override
    public void maintenanceOver(MaintenanceOver.MaintenanceOverRequest request, StreamObserver<MaintenanceOver.MaintenanceOverResponse> responseObserver) {
        Maintenance.increaseConsensus();
        LaunchRobot.getMaintenance().notifyMaintenance();
        responseObserver.onNext(MaintenanceOver.MaintenanceOverResponse.newBuilder().setReceived("New OK received").build());
        responseObserver.onCompleted();
    }

}

