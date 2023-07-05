package CleaningRobot.Initialization;

import java.util.Random;
import static Parameters.ConnectionParameters.getAdminServerAddress;
import static Parameters.RobotParameters.getMaxRobot;
import static Parameters.RobotParameters.myPort;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Robot {

    private String ID;
    private String address;
    private int port;


    public Robot(String ID, String address, int port) {
        this.ID= ID;
        this.address = address;
        this.port = port;
    }

    public Robot() {
        this.ID= ((Integer)(new Random().nextInt(getMaxRobot()))).toString();
        this.address = getAdminServerAddress();
        this.port = myPort();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port){
        this.port=port;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address=address;
    }

    public String toString() {
        return "Robot n: "+this.getID();
    }
}
