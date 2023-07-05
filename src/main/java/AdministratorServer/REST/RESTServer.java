package AdministratorServer.REST;

import AdministratorServer.MQTT.ArrangePollutionDataToClient;
import AdministratorServer.MQTT.PollutionSubscriber;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

import static Parameters.ConnectionParameters.*;

public class RESTServer {

    private static final String HOST = getAdminServerAddress();
    private static final int PORT = getServerPort();
    private static ArrangePollutionDataToClient arrange = new ArrangePollutionDataToClient();
    private static PollutionSubscriber subscriber;
    private static int[] counterCell = new int[]{0,0,0,0};

    public static void main(String[] args) throws IOException, MqttException {

        //REST Server
        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
        server.start();
        System.out.println("Server running!");
        System.out.println("Server started on: http://"+HOST+":"+PORT);

        //MQTT Server
        subscriber = new PollutionSubscriber();
        subscriber.mqttServer();

        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");

        System.exit(0);
    }

    public static ArrangePollutionDataToClient getArrange() {
        synchronized (arrange) {
            return arrange;
        }

    }

    public static int[] getCounterCell() {return counterCell;}

}




