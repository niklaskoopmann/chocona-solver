package chocona.structure;

import java.util.ArrayList;
import java.util.Arrays;

public class Field {

    private final int width;
    private final int height;
    private final ArrayList<Region> regions;
    public char[][] solution;

    public Field(int width, int height, ArrayList<Region> regions) {
        this.width = width;
        this.height = height;
        this.regions = regions;
    }

    public Field(char[][] solution) {
        this.solution = solution;
        this.width = solution[0].length;
        this.height = solution.length;
        this.regions = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public char[][] getSolution() {
        return solution;
    }

    public void setSolution(char[][] solution) {
        if (solution.length == this.height && solution[0].length == this.width) this.solution = solution;
        else throw new IllegalArgumentException("Dimensions for field solution do not match field dimensions!");
    }

    @Override
    public String toString() {
        return "Field{" +
                "width=" + width +
                ", height=" + height +
                ", regions=" + regions +
                ", solution=" + Arrays.toString(solution) +
                '}';
    }
}
