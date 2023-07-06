package CleaningRobot.Initialization;

import static Parameters.ConnectionParameters.*;
import AdministratorServer.REST.Robots;
import AdministratorServer.REST.StartRobotListResponse;
import CleaningRobot.GRPC.GRPCClient;
import CleaningRobot.GRPC.GRPCServer;
import CleaningRobot.GRPC.SendHeartbeat;
import CleaningRobot.GoToMechanic.Maintenance;
import CleaningRobot.Simulator.PM10Simulator;
import CleaningRobot.SlidingWindow.AverageConsumer;
import CleaningRobot.SlidingWindow.SlidingWindow;
import CleaningRobot.SlidingWindow.PollutionPublisher;
import SmartCity.District;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class LaunchRobot {

    private static Client client = Client.create();
    private static String serverAddress = "http://"+getAdminServerAddress()+":"+getServerPort();
    private static ClientResponse clientResponse = null;
    private static final String welcome = getPathWelcome();
    private static final String addRobot = getPathAddRobot();
    private static final String removeRobot = getRemoveRobot();
    private static volatile boolean fixRobot = false;
    private static BufferedReader reader;
    private static Maintenance m;

    private static final Object lockFixRobot = new Object();

    public static void main(String args[]) throws MqttException, IOException {

        //Welcome page
        clientResponse = getRequest(client,serverAddress+welcome);
        System.out.println(clientResponse.getEntity(String.class));

        //ADD ROBOT
        Robot r = new Robot();
        StartRobotListResponse sr = initializeRobot(client, serverAddress, addRobot, r);

        //Presenting itself with GRPC...
        GRPCServer grpcServer = new GRPCServer(r.getID(), r.getPort(), sr.getRobotList());
        grpcServer.startServer();
        GRPCClient grpcClient= new GRPCClient(r,sr,sr.getCell());
        grpcClient.presenting();

        //Measuring...
        SlidingWindow sw = new SlidingWindow();
        PM10Simulator simulator = new PM10Simulator(sw);
        AverageConsumer avgConsumer = new AverageConsumer(sw, r.getID(), District.getDistrict(sr.getCell()));

        //Publishing...
        simulator.start();
        avgConsumer.start();
        PollutionPublisher pp = new PollutionPublisher(r.getID(), District.getDistrict(sr.getCell()), avgConsumer.getAverageList());
        //pp.start();  //TODO

        //Check heartbeats of the other robots
        SendHeartbeat sh = new SendHeartbeat(grpcClient);
        sh.start();

        //Mechanic
        m = new Maintenance(grpcClient, sr.getRobotList());
        m.start();

        System.out.println("Insert \"quit\" for leaving the system in a proper way or \n \"fix\" for going to the mechanic");
        reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            switch (reader.readLine()) {
                //Fixing...
                case "fix":
                    if(isFixRobot()) {
                        System.out.println("Robot already at the mechanic!");
                    }

                    else {
                        setFixRobot(true);
                        System.out.println("Trying to fix you");

                    }
                    break;
                //Quitting...
                case "quit":
                    System.out.println("Leaving System: finish to execute maintenance...");
                    try {
                        Maintenance.setStopCondition(true);
                        m.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //Comunicate to other robots through GRPC
                    grpcClient.goodbye();
                    grpcServer.shutdown();
                    //Stop Threads
                    simulator.stopMeGently();
                    avgConsumer.setStopCondition(true);
                    pp.setStopCondition(true);
                    sh.setStopCondition(true);
                    //Comunicate the Server via REST (DELETE)
                    clientResponse = deleteRequest(client, serverAddress + removeRobot, r);
                    System.out.println(clientResponse.toString());
                    System.out.println("EXIT");
                    System.exit(0);  //with server shutted down doesn't work
            }
        }

    }

    public static ClientResponse postRequest(Client client, String url, Robot r){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(r);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }

    public static ClientResponse deleteRequest(Client client, String url, Robot r){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(r);
        try {
            return webResource.type("application/json").delete(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }

    public static ClientResponse getRequest(Client client, String url){
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }


    public static StartRobotListResponse initializeRobot(Client client, String serverAddress, String path, Robot robot) {
        System.out.println("Launching to the server the new robot with ID: "+robot.getID());
        ClientResponse clientResponse = postRequest(client,serverAddress+path,robot);
        StartRobotListResponse sr = clientResponse.getEntity(StartRobotListResponse.class);
        //System.out.println("List for saying hello: "+sr.getRobotList());
        System.out.println(clientResponse);
        return sr;
    }

    public static void getRobotList(Client client, String serverAddress, String path) {
        ClientResponse clientResponse = getRequest(client,serverAddress+path);
        System.out.println(clientResponse.toString());
        Robots robots = clientResponse.getEntity(Robots.class);
        System.out.println("Robots List:");
        System.out.println("Size: "+ robots.getRobotsList().size());
        for (Robot r : robots.getRobotsList()){
            System.out.println("\tID: " + r.getID()+"; Address: "+r.getAddress()+"; Port: "+r.getPort()+";");
        }
    }

    public static Client getClient() {
        return client;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static boolean isFixRobot() {

        synchronized (lockFixRobot) {return fixRobot;}
    }

    public static void setFixRobot(boolean fixRobot) {
        synchronized (lockFixRobot) {
            LaunchRobot.fixRobot = fixRobot;
        }
    }


    public static Maintenance getMaintenance() {
        synchronized (m) {
            return m;
        }
    }


}

