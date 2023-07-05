package CleaningRobot.SlidingWindow;

import CleaningRobot.Simulator.*;
import java.util.ArrayList;
import java.util.List;
import static Parameters.SlidingWindowParameters.*;

public class SlidingWindow implements Buffer {
    int numberOfMeasures;
    double overlap;

    public List<Measurement> measurements = new ArrayList<Measurement>();


    public SlidingWindow() {
        numberOfMeasures = getNumberOfMeasurements();
        overlap = getOverlap();
    }

    @Override
    public synchronized void addMeasurement(Measurement m) {
        measurements.add(m);
        if (measurements.size()>=numberOfMeasures) {
            notify();
        }
    }

    @Override
    public synchronized List<Measurement> readAllAndClean()  {
        List<Measurement> copy = null;
        while(measurements.size()<numberOfMeasures) {
            try {
                wait();
            } catch (InterruptedException e) { e.printStackTrace();}
        }
        copy = new ArrayList<Measurement>(measurements);
        //refresh the list based on the overlap
        measurements.subList(0, (int) (numberOfMeasures*overlap)).clear();
        return copy;
    }


}
