package SmartCity;

import AdministratorServer.REST.RESTServer;

import java.util.Random;

public class CellFactory {

    //if is not possible for robots to be in the same cell
    //private static List<Cell> usedCells = new ArrayList<Cell>();
    //private static int[] counterCell = new int[]{0,0,0,0};

    public static Cell makeCell() {
        synchronized (RESTServer.getCounterCell()) {
            int dis = getMinorIndex(RESTServer.getCounterCell());
            return makeCell(dis+1);
        }

        /*if (usedCells.contains(tmp)) {
            //System.out.println("Existed :"+tmp);
            return makeCell();
        }
        else {
            usedCells.add(tmp);
            return tmp;
        }*/
    }

    private static Cell makeCell(int district) {
        synchronized (RESTServer.getCounterCell()) {
            int rowCell = new Random().nextInt(Grid.row/2) + ((district==1 || district==4)?0 :5);
            int columnCell = new Random().nextInt(Grid.column/2) + ((district==1 || district==2)?0 :5);
            Cell c = new Cell(rowCell,columnCell);
            RESTServer.getCounterCell()[district-1]++;
            return c;
        }

    }


    private static int getMinorIndex(int[] array) {
        int ind = 0;
        int min = array[ind];
        for (int i=1; i< array.length; i++) {
            if (array[i] <  min) {
                min=array[i];
                ind=i;
            }
        }
        System.out.println(ind);
        return ind;
    }

}
