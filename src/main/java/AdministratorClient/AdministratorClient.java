package AdministratorClient;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static Parameters.ConnectionParameters.*;
import static CleaningRobot.Initialization.LaunchRobot.getRobotList;

public class AdministratorClient {

    public static void main(String[] args) throws Exception{
        Client client = Client.create();
        String serverAddress = "http://"+getAdminServerAddress()+":"+getServerPort();
        ClientResponse clientResponse = null;
        final String listRobots= getPathRobotList();
        final String avgNPollutions = getClientAvgNPollutions();
        final String overallAvg = getClientOverallAvg();
        System.out.println("Hello User!");
        while (true) {
            System.out.println("Insert 1 to obtain the list of robots currently located in Greenfield");
            System.out.println("Insert 2 to obtain the average of of the last n air pollution levels sent to the server by a given robot");
            System.out.println("Insert 3 to obtain the average of the air pollution levels sent by all the robots to the server and occurred from timestamps t1 and t2\n" +
                    "The result -1.0 is due to the fact that the timestamp inserted are less than the minor in the server ");
            System.out.println("Insert 0 to abort the system");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine();

            switch (choice) {
                case "0":
                    System.exit(0);
                case "1":
                    System.out.println("The list of the robots currently located in Greenfield:");
                    getRobotList(client, serverAddress, listRobots);
                    break;

                case "2":
                    System.out.println("Insert the ID of the robot you want to check:");
                    //reader = new BufferedReader(new InputStreamReader(System.in));
                    String id = reader.readLine();
                    System.out.println("Insert the number of air pollutions levels you want to obtain:");
                    int number = Integer.parseInt(reader.readLine());
                    clientResponse = postRequest(client, serverAddress + avgNPollutions, new Statistics(id, number));
                    System.out.println(clientResponse);
                    Double n = Double.parseDouble(clientResponse.getEntity(String.class));
                    System.out.println(n);
                    break;

                case "3":
                    System.out.println("Insert Timestamp 1:");
                    Long t1 = Long.parseLong(reader.readLine());
                    System.out.println("Insert Timestamp 2:");
                    Long t2 = Long.parseLong(reader.readLine());
                    if (t2 < t1) {
                        long t3 = t1;
                        t1 = t2;
                        t2 = t3;
                    }
                    clientResponse = postRequest(client, serverAddress + overallAvg, new Statistics(t1, t2));
                    System.out.println(clientResponse);
                    n = Double.parseDouble(clientResponse.getEntity(String.class));
                    System.out.println(n);
                    break;
            }
        }
    }

    public static ClientResponse postRequest(Client client, String url, Statistics s){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(s);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            e.printStackTrace();
            return null;
        }
    }
}
