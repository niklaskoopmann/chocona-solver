package structure;

import java.util.ArrayList;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }
}
