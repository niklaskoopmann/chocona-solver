package structure;

import java.util.ArrayList;

public class Region {

    public int targetNumber;
    public int totalCells;
    public ArrayList<Cell> cells;

    public Region(int targetNumber, int totalCells, ArrayList<Cell> cells) {
        this.targetNumber = targetNumber;
        this.totalCells = totalCells;
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "Region{" +
                "targetNumber=" + targetNumber +
                ", totalCells=" + totalCells +
                ", cells=" + cells +
                '}';
    }
}
