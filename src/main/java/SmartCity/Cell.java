package SmartCity;

public class Cell {
    public int rowCell=-1;
    public int columnCell=-1;

    public Cell() {}

    public Cell(int row, int column) {
        this.rowCell=row;
        this.columnCell=column;
    }


    public String toString() {
        return rowCell+","+columnCell;
    }

    @Override
    public boolean equals(Object other) {
        try {
            Cell cell = (Cell) other;
            return (this.rowCell == cell.rowCell) && (this.columnCell == cell.columnCell);
        } catch (Exception e) { e.printStackTrace();}
        return false;
    }
}
