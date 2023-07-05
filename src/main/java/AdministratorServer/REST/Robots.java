package AdministratorServer.REST;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import CleaningRobot.Initialization.Robot;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Robots {

    @XmlElement(name="my_robots")
    private List<Robot> robotslist;

    private static Robots instance;

    private Robots() {

        robotslist=new ArrayList<Robot>();
    }

    public synchronized static Robots getInstance(){
        if(instance==null)
            instance = new Robots();
        return instance;
    }

    public synchronized List<Robot> getRobotsList() {

        return new ArrayList<>(robotslist);

    }

    public synchronized boolean isRobotAlreadyRegistered(String ID) {

        return robotslist.contains(getByID(ID));

    }

    public synchronized void setRobotslist(List<Robot> robotslist) {
        this.robotslist = robotslist;
    }

    public synchronized void add(Robot r){
        System.out.println("RobotListServerAdd: "+getRobotsList());
        robotslist.add(r);
        System.out.println("RobotListServerAdd: "+getRobotsList());

    }

    public synchronized void remove(Robot r){
        System.out.println("RobotListServerRemove: "+getRobotsList());
        removeRobot(r,getRobotsList());
        System.out.println("RobotListServerRemove: "+getRobotsList());
    }


    public Robot getByID(String ID){
        List<Robot> robotsCopy = getRobotsList();
        for(Robot r: robotsCopy)
            if(r.getID().equals(ID))
                return r;
        return null;
    }

    public void removeRobot(Robot r, List<Robot> listRobot) {
        for (int i=0; i<listRobot.size(); i++) {
            if(listRobot.get(i).getID().equals(r.getID())) {
                this.robotslist.remove(i);
                break;
            }
        }
    }

}