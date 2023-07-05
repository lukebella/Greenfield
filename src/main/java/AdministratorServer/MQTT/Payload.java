package AdministratorServer.MQTT;

import java.util.List;

public class Payload {
    private String ID;
    private long timestamp;
    private List<Double> avgs;

    public Payload(String ID, long timestamp, List<Double> avgs) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.avgs = avgs;
    }

    //Constructor for hashmap value for sending data to client (avoiding redundancy on ID)
    public Payload(long timestamp, List<Double> avgs) {
        this.timestamp = timestamp;
        this.avgs = avgs;
    }

    public String getID() {
        return ID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Double> getAvgs() {
        return avgs;
    }
}
