package chocona.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import chocona.structure.Cell;
import chocona.structure.Field;
import chocona.structure.Region;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class InputParser {

    private JSONObject jsonObject;

    public InputParser() {
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void parseFile(Path pathToJsonFile) throws java.io.IOException, ParseException {

        String rawInput = Files.readString(pathToJsonFile, Charset.defaultCharset());
        jsonObject = (JSONObject) new JSONParser().parse(rawInput);
    }

    public Field parseToField(JSONObject jsonObject) {

        ArrayList<Region> parsedRegions = new ArrayList<>();

        JSONArray shapes = (JSONArray) jsonObject.get("shapes");

        // use final arrays to set from inside lambda expressions
        final int[] maxX = {0};
        final int[] maxY = {0};

        shapes.forEach(shape -> {

            JSONArray cells = (JSONArray) (((JSONObject) (shape)).get("fields"));
            ArrayList<Cell> parsedCells = new ArrayList<>();

            cells.forEach(cell -> {

                JSONObject castCell = (JSONObject) cell;
                int currentX = (int)(long)castCell.get("x");
                int currentY = (int)(long)castCell.get("y");
                Cell currentCellParsed = new Cell(currentX, currentY);

                // check for new maximum x/y coordinates
                if (currentX > maxX[0]) maxX[0] = currentX;
                if (currentY > maxY[0]) maxY[0] = currentY;

                parsedCells.add(currentCellParsed);
            });

            parsedRegions.add(new Region((int)(long)((JSONObject)shape).get("number"), parsedCells.size(), parsedCells));
        });

        return new Field(maxX[0] + 1, maxY[0] + 1, parsedRegions);
    }

    @Override
    public String toString() {
        return "InputParser{" +
                "jsonObject=" + jsonObject +
                '}';
    }
}
