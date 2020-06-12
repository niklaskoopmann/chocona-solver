package chocona.gui;

import chocona.logic.InputParser;
import chocona.structure.Field;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerTest {

    private Controller controller;
    private Field field;
    private InputParser parser;
    private AnchorPane anchorPane;
    private Label output;
    private JFXPanel jfxPanel;

    @BeforeEach
    void setUp() {

        controller = new Controller();
        parser = new InputParser();
        try {
            parser.parseFile(Path.of("./test/input_data/01.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        field = parser.parseToField(parser.getJsonObject());

        jfxPanel = new JFXPanel();
        anchorPane = new AnchorPane();
        output = new Label();

        controller.setBoardPane(anchorPane);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void drawField() {
        controller.drawField(field);
        assertEquals(36, controller.getBoardPane().getChildren().size());
    }

    @Test
    void updateField() {

        // initialize field drawn on anchor pane
        controller.drawField(field);

        // generate some solution
        char[][] solution = new char[6][6];
        for (int i = 0; i < 6; i++) {
            Arrays.fill(solution[i], 'B');
        }

        // set solution for field
        field.setSolution(solution);

        // update the displayed field
        controller.updateField(field);

        // check that every cell rectangle in the field is black
        controller
                .getBoardPane()
                .getChildren()
                .forEach((Node p) -> {
                    ((Pane) p).getChildren()
                            .stream()
                            .filter(n -> n instanceof StackPane)
                            .forEach(s -> ((StackPane) s)
                                    .getChildren()
                                    .forEach(c -> {
                                        if (c instanceof Rectangle)
                                            assertEquals(Color.gray(0.2), ((Rectangle) c).getFill());
                                    })
                            );
                });

        // generate other solution to update field with
        char[][] newSolution = new char[6][6];
        for (int i = 0; i < 6; i++) {
            Arrays.fill(solution[i], 'W');
        }

        // set solution for field
        field.setSolution(solution);

        // update the displayed field
        controller.updateField(field);

        // check that every cell rectangle in the field is now white
        controller
                .getBoardPane()
                .getChildren()
                .forEach((Node p) -> {
                    ((Pane) p).getChildren()
                            .stream()
                            .filter(n -> n instanceof StackPane)
                            .forEach(s -> ((StackPane) s)
                                    .getChildren()
                                    .forEach(c -> {
                                        if (c instanceof Rectangle)
                                            assertEquals(Color.WHITE, ((Rectangle) c).getFill());
                                    })
                            );
                });
    }


    @Test
    void setOutputText() {
        controller.setOutputTextLabel(output);
        controller.setOutputText("Lorem ipsum");
        assertEquals("Lorem ipsum", controller.getOutputTextLabel().getText());
    }

    @Test
    void resetBoardPane() {
        controller.drawField(field);
        assertEquals(36, controller.getBoardPane().getChildren().size());
        controller.resetBoardPane();
        assertEquals(0, controller.getBoardPane().getChildren().size());
    }
}