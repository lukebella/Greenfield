package SmartCity;

public class District {
    private static final int row = Grid.row;
    private static final int column = Grid.column;
    private static final int numDistricts = 4;

    public static int getDistrict(Cell c) {
        if ((c.rowCell < row/2) && (c.columnCell<column/2))
            return 1;
        if ((c.rowCell >= row/2) && (c.columnCell<column/2))
            return 4;
        if ((c.rowCell <= row/2) && (c.columnCell>=column/2))
            return 2;
        else return 3;
    }

    public static int getNumDistricts() {
        return numDistricts;
    }
}
