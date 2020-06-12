package chocona.logic;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chocona.structure.Cell;
import chocona.structure.Field;
import chocona.structure.Region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InputParserTest {

    InputParser inputParser;
    String input01;
    String inputInvalid;
    Field expected;
    Region inExpected;

    @BeforeEach
    void setUp() {
        inputParser = new InputParser();
        input01 = System.getProperty("user.dir") + File.separator + "test" + File.separator + "input_data" + File.separator + "01.json";
        inputInvalid = System.getProperty("user.dir") + File.separator + "test" + File.separator + "input_data" + File.separator + "invalid.json";

        ArrayList<Cell> cellsInExpected = new ArrayList<Cell>();
        cellsInExpected.add(new Cell(0,0));
        cellsInExpected.add(new Cell(1,0));
        cellsInExpected.add(new Cell(0,1));
        inExpected = new Region(3,3,cellsInExpected);
        ArrayList<Region> testRegion = new ArrayList<Region>();
        testRegion.add(inExpected);
        expected = new Field(6,6, testRegion);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getJsonObject() {
    }

    @Test
    void parseFilePositive() {
        try {
            inputParser.parseFile(Path.of(input01));
        } catch (Exception e) {
            assertNull(e);
        }
    }

    @Test
    void parseFileNegative() {
        Exception e1 = assertThrows(IOException.class, () -> inputParser.parseFile(Path.of("helloworld")));
        Exception e2 = assertThrows(ParseException.class, () -> inputParser.parseFile(Path.of(inputInvalid)));
    }

    @Test
    void parseToField() {
        try {
            inputParser.parseFile(Path.of(input01));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field parsed = inputParser.parseToField(inputParser.getJsonObject());

        // dimensions
        assertEquals(expected.getWidth(), parsed.getWidth());
        assertEquals(expected.getHeight(), parsed.getHeight());

        // content
        assertEquals(expected.getRegions().get(0).targetNumber, parsed.getRegions().get(0).targetNumber);
        assertEquals(expected.getRegions().get(0).totalCells, parsed.getRegions().get(0).totalCells);
        for (int i = 0; i < expected.getRegions().get(0).getCells().size(); i++) {
            assertEquals(expected.getRegions().get(0).getCells().get(i).getX(), parsed.getRegions().get(0).getCells().get(i).getX());
            assertEquals(expected.getRegions().get(0).getCells().get(i).getY(), parsed.getRegions().get(0).getCells().get(i).getY());
        }
    }

    @Test
    void testToString() {

        assertTrue(inputParser.toString().contains("InputParser{"));
        assertTrue(inputParser.toString().contains("jsonObject="));
    }
}