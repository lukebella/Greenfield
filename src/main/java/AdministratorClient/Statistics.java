package AdministratorClient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics {
    private String ID;
    private long t1,t2;
    private int n;


    public Statistics() {}
    public Statistics(String ID, int n) {
        this.ID = ID;
        this.n=n;
    }

    public Statistics(long t1, long t2) {
        this.t1=t1;
        this.t2=t2;
    }

    public String getID() {
        return ID;
    }
    public int getN() {
        return n;
    }
    public long getT1() {
        return t1;
    }
    public long getT2() {
        return t2;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "ID='" + ID + '\'' +
                ", n=" + n +
                '}';
    }
}
