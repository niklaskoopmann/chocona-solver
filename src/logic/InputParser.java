package logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import structure.Cell;
import structure.Region;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class InputParser {

    private JSONObject jsonObj;

    public InputParser() {
    }

    public JSONObject getJsonObj() {
        return jsonObj;
    }

    public void parseFile(Path pathToJsonFile) throws java.io.IOException, ParseException {

        String rawInput = Files.readString(pathToJsonFile, Charset.defaultCharset());

        JSONParser prs = new JSONParser();

        jsonObj = (JSONObject) prs.parse(rawInput);
    }

    public ArrayList<Region> parseToField(JSONObject jsonObject) {

        ArrayList<Region> parsedRegions = new ArrayList<>();

        JSONArray shapes = (JSONArray) jsonObject.get("shapes");

        shapes.forEach(shape -> {

            JSONArray cells = (JSONArray) (((JSONObject) (shape)).get("fields"));
            ArrayList<Cell> parsedCells = new ArrayList<>();

            cells.forEach(cell -> {

                JSONObject castCell = (JSONObject) cell;
                Cell currentCellParsed = new Cell((long) castCell.get("x"), (long) castCell.get("y"));
                parsedCells.add(currentCellParsed);
            });

            parsedRegions.add(new Region((long) ((JSONObject)shape).get("number"), parsedCells.size(), parsedCells));
        });

        return parsedRegions;
    }

    @Override
    public String toString() {
        return "InputParser{" +
                "jsonObj=" + jsonObj +
                '}';
    }
}
