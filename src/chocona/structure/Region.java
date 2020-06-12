package chocona.structure;

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

    public Cell getCellRelative(Cell cell, int stepsToRight, int stepsToBottom) {
        int targetX = cell.getX() + stepsToRight;
        int targetY = cell.getY() + stepsToBottom;
        for (Cell c : this.cells) {
            if (c.getX() == targetX && c.getY() == targetY) return c;
        }
        return null;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public int getTotalCells() {
        return totalCells;
    }

    public ArrayList<Cell> getCells() {
        return cells;
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
