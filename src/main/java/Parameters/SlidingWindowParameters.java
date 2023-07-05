package Parameters;

public class SlidingWindowParameters {
    private static final int numberOfMeasurements = 8;
    private static final double overlap = 0.5;

    public static int getNumberOfMeasurements() {
        return numberOfMeasurements;
    }

    public static double getOverlap() {
        return overlap;
    }

}
