package CleaningRobot.SlidingWindow;

import CleaningRobot.Simulator.Measurement;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

public class AverageConsumer extends Thread{

    private final SlidingWindow sw;
    private static List<Double> averageList;
    private volatile boolean stopCondition=false;

    public AverageConsumer(SlidingWindow sw, String ID, int district)  {
        this.sw= sw;
        averageList = new ArrayList<>();
    }

    public void run() {
        while (!stopCondition) {
            //put average in the list
                averageList.add(makeAvg(sw.readAllAndClean()));
        }
    }

    private double makeAvg(List<Measurement> measures) {
        return measures.stream().mapToDouble(Measurement::getValue).average().orElse(Double.NaN);
    }

    public List<Double> getAverageList() {
        return averageList;
    }

    public void setStopCondition(boolean stopCondition) {
        this.stopCondition = stopCondition;
    }

}
