package logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

        System.out.println(rawInput);

        JSONParser prs = new JSONParser();

        jsonObj = (JSONObject) prs.parse(rawInput);
    }

    public ArrayList<Region> parseToField(JSONObject jsonObject) {

        ArrayList<Region> parsedRegions = new ArrayList<Region>();

        jsonObject.

        return parsedRegions;
    }

    @Override
    public String toString() {
        return "InputParser{" +
                "jsonObj=" + jsonObj +
                '}';
    }
}
