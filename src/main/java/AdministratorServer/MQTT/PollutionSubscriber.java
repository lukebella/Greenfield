package AdministratorServer.MQTT;

import AdministratorServer.REST.RESTServer;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import static Parameters.ConnectionParameters.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PollutionSubscriber {
    MqttClient client;
    String broker;
    String clientId;
    String topic;
    int qos;
    MqttConnectOptions connOpts;

    public PollutionSubscriber() throws MqttException {
        broker = getBroker();
        clientId = getNameMQTTServer(); //or MqqtClient.generateClientId;
        topic = getTopic();
        qos = 2;
        client = new MqttClient(broker, clientId);
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
    }

    public void mqttServer() {
        try {
            System.out.println(this.clientId + " Connecting Broker " + broker);
            client.connect(connOpts);
            System.out.println(clientId + " Connected - Thread PID: " + Thread.currentThread().getId());

            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    Payload receivedMessage = new Gson().fromJson(new String(message.getPayload()), Payload.class);
                    PollutionSubscriber.arrangeData(RESTServer.getArrange(), receivedMessage);
                    System.out.println("Pollut. Sub ID: "+receivedMessage.getID());
                    System.out.println(clientId +" Received a Message! - Callback - Thread PID: " + Thread.currentThread().getId() +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic);

                    System.out.println("\n ***  Press a random key to exit *** \n");

                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });
            System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());

            for (int i=1; i<=getNumDistricts(); i++) {
                client.subscribe(topic+"{"+i+"}",qos);
            }

            System.out.println("\n ***  Press a random key to exit *** \n");
            Scanner command = new Scanner(System.in);
            command.nextLine();
            client.disconnect();

        } catch (MqttException me ) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    public static void arrangeData(ArrangePollutionDataToClient arrange, Payload receivedMessage) {
        synchronized (arrange) {
            List<Payload> payloads = new ArrayList<Payload>();
            try {
                if (arrange.isMapped(receivedMessage.getID())) {

                    payloads = arrange.getListPayload(receivedMessage.getID());
                    arrange.remove(receivedMessage.getID());
                    payloads.add(new Payload(receivedMessage.getTimestamp(), receivedMessage.getAvgs()));
                    arrange.setRobotAvg(receivedMessage.getID(), payloads);

                    //System.out.println("The Average: "+arrange.getNAirPollutions(receivedMessage.getID(), 1));
                }
                else {
                    payloads.add(new Payload(receivedMessage.getTimestamp(), receivedMessage.getAvgs()));
                    arrange.setRobotAvg(receivedMessage.getID(), payloads);
                }

            } catch(Exception e) {e.printStackTrace();}
        }

    }


}
