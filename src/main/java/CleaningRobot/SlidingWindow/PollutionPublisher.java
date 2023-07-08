package CleaningRobot.SlidingWindow;

import AdministratorServer.MQTT.Payload;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

import static Parameters.ConnectionParameters.*;

public class PollutionPublisher extends Thread{
    MqttClient client;
    private String broker;
    private String clientId;
    private String topic = getTopic(); // remember that {i} is the district of the robot
    private final int qos;
    private volatile boolean stopCondition = false;
    private final List<Double> averageList;


    public PollutionPublisher(String clientId, int district, List<Double> averageList) throws MqttException {
        broker = getBroker();
        this.clientId=clientId;
        client = new MqttClient(broker, clientId);
        this.topic = this.topic+"{"+district+"}";
        qos=2;
        this.averageList = averageList;
    }

    @Override
    public void run() {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            System.out.println(clientId + " Connecting Broker " + broker);
            client.connect(connOpts);
            System.out.println(clientId + " Connected");
            System.out.println("AverageList: "+averageList);
            while(!stopCondition) {
                Thread.sleep(15000);
                synchronized (averageList) {
                    System.out.println("ID: " + this.clientId + "; Timestamp: " + System.currentTimeMillis() + "; AverageList: " + averageList);
                    String payload = new Gson().toJson(new Payload(this.clientId, System.currentTimeMillis(), averageList));
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    message.setQos(qos);
                    System.out.println(clientId + " Publishing message: " + payload + " ...");
                    client.publish(topic, message);
                    averageList.clear();
                    System.out.println(clientId + " Message published");
                }

            }


        } catch (MqttException me ) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStopCondition(boolean stopCondition) {
        this.stopCondition = stopCondition;
    }

}
