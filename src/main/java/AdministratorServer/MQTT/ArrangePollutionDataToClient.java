package AdministratorServer.MQTT;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrangePollutionDataToClient {

    private HashMap<String, List<Payload>> idAndPayload = new HashMap<String, List<Payload>>();
    public ArrangePollutionDataToClient() {}

    public void setRobotAvg(String ID, List<Payload> p) {
        idAndPayload.put(ID, p);
    }

    //The average of the air pollution levels sent by all the robots to the server and occurred from timestamps t1 and t2
    public Double getOverallAveragePollutions(long t1, long t2) {
        List<Double> avgRobot = new ArrayList<Double>();
        for (Object k : idAndPayload.keySet().toArray()) {
            String key = (String)k;
            List<Payload> payloads = idAndPayload.get((String)key);
            List<Double> avgs = new ArrayList<>();
            for (int j = 0; j < payloads.size(); j++) {
                if (payloads.get(j).getTimestamp() >= t1 && payloads.get(j).getTimestamp() <= t2)
                    avgs.addAll(payloads.get(j).getAvgs());
            }
            /*System.out.println("Avgs: "+avgs);
            System.out.println("idandpayload :"+ idAndPayload);*/
            Double t = makeAvg(avgs);
            if (!Double.isNaN(t)) avgRobot.add(t);

        }
        Double res = makeAvg(avgRobot);
        if(res.isNaN()) {return Double.parseDouble("-1");}
        else return res;
    }

    //The average of the last n air pollution levels sent to the server by a given robot
    public Double getNAirPollutions(String ID, int n) {
        List<Payload> tmp = idAndPayload.get(ID);

        List<Double> values=new ArrayList<Double>();
        for (int i= tmp.size()-1;i>=tmp.size()-n; i--) {
            values.add(makeAvg(tmp.get(i).getAvgs()));
        }
        return makeAvg(values);
    }

    private Double makeAvg(List<Double> m) {
        return m.stream().mapToDouble(Double::valueOf).average().orElse(Double.NaN);
    }

    public List<Payload> getListPayload(String ID) {
        return idAndPayload.get(ID);
    }

    public boolean isMapped(String key) {
        return idAndPayload.containsKey(key);
    }

    public void remove(String key) {
        idAndPayload.remove(key);
    }
}
