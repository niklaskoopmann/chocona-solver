package structure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RegionTest {

    Region region;

    @BeforeEach
    void setUp() {
        ArrayList<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(0,1));
        cells.add(new Cell(1,0));
        cells.add(new Cell(1,1));
        cells.add(new Cell(1,2));
        cells.add(new Cell(2,0));
        cells.add(new Cell(2,1));
        region = new Region(3, 6,cells);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCellRelative() {
        Cell cell = new Cell(1,1);
        Cell toRight = region.getCellRelative(cell, 1, 0);
        Cell toLeft = region.getCellRelative(cell, -1, 0);
        Cell below = region.getCellRelative(cell, 0, 1);
        Cell above = region.getCellRelative(cell, 0, -1);
        Cell outside = region.getCellRelative(cell, 2, 0);
        assertEquals(2, toRight.getX());
        assertEquals(1, toRight.getY());
        assertEquals(0, toLeft.getX());
        assertEquals(1, toLeft.getY());
        assertEquals(1, below.getX());
        assertEquals(2, below.getY());
        assertEquals(1, above.getX());
        assertEquals(0, above.getY());
        assertNull(outside);
    }

    @Test
    void testToString() {
        assertTrue(region.toString().contains("targetNumber=3"));
        assertTrue(region.toString().contains("totalCells=6"));
        assertTrue(region.toString().contains("totalCells="));
    }

    @Test
    void getTargetNumber() {
        assertEquals(3, region.getTargetNumber());
    }

    @Test
    void getTotalCells() {
        assertEquals(6, region.getTotalCells());
    }
}