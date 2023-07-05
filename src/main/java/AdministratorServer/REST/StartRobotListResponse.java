package AdministratorServer.REST;

import CleaningRobot.Initialization.Robot;
import SmartCity.Cell;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static SmartCity.CellFactory.makeCell;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StartRobotListResponse {

    private Cell cell;
    private List<Robot> robotList;
    public StartRobotListResponse() {
        this.robotList=new ArrayList<Robot>();
    }

    public StartRobotListResponse(List<Robot> robotList) {
        this.robotList= robotList;
        this.cell=makeCell();
        /*if (this.robotList.isEmpty()) {
            System.out.println("list empty");

            int i =counterCell.get(District.getDistrict(this.cell) -1) +1;
            counterCell.add(District.getDistrict(this.cell) -1,i);
        }
        else {
            System.out.println("list not empty");*/

        //}
    }


    public Cell getCell() {
        return this.cell;
    }

    public List<Robot> getRobotList() {
        synchronized(robotList) {
            return robotList;
        }
    }


}
