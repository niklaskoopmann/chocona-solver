package chocona.structure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    Field fieldFromCharArray;
    Field fieldFromValues;

    @BeforeEach
    void setUp() {
        fieldFromCharArray = new Field(new char[7][2]);
        fieldFromValues = new Field(2, 7, new ArrayList<Region>());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWidth() {
        assertEquals(2, fieldFromValues.getWidth());
        assertEquals(2, fieldFromCharArray.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(7, fieldFromValues.getHeight());
        assertEquals(7, fieldFromCharArray.getHeight());
    }

    @Test
    void getRegions() {
    }

    @Test
    void setGetSolution() {
        char[][] fittingSolution = new char[][]{
                {'A', 'B'},
                {'C', 'D'},
                {'E', 'F'},
                {'G', 'H'},
                {'I', 'J'},
                {'K', 'L'},
                {'M', 'N'}
        };

        char[][] impossibleSolution = new char[][]{
                {'A', 'B', 'C'},
                {'D', 'E', 'F'},
                {'G', 'H', 'I'}
        };

        fieldFromCharArray.setSolution(fittingSolution);
        assertEquals(fittingSolution, fieldFromCharArray.getSolution());

        Exception e = assertThrows(IllegalArgumentException.class, () -> fieldFromValues.setSolution(impossibleSolution));
        assertEquals("Dimensions for field solution do not match field dimensions!", e.getMessage());
    }

    @Test
    void testToString() {
        assertTrue(fieldFromValues.toString().contains("Field"));
        assertTrue(fieldFromValues.toString().contains("width=2"));
        assertTrue(fieldFromValues.toString().contains("height=7"));
        assertTrue(fieldFromValues.toString().contains("regions="));
        assertTrue(fieldFromValues.toString().contains("solution="));
    }
}