package gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import structure.Cell;
import structure.Field;
import structure.Region;

import java.util.Random;

public class Controller {

    @FXML
    private AnchorPane boardPane;
    @FXML
    private Label outputText;

    public void drawField(Field field) {

        // calculate width and height of each square for display
        double singleSquareWidth = (boardPane.getPrefWidth() / field.getWidth()) / 1.5;
        double singleSquareHeight = (boardPane.getPrefHeight() / field.getHeight()) / 1.5;

        for (Region r : field.getRegions()) {

            // randomize cell which displays target number
            int number = r.targetNumber;
            Random rand = new Random();
            int rectangleWithNumberIndex = rand.nextInt(r.totalCells);

            for (int i = 0; i < r.cells.size(); i++) {

                Cell cell = r.cells.get(i);

                // create a white rectangle for current cell
                Rectangle rect = new Rectangle();
                rect.setWidth(singleSquareWidth);
                rect.setHeight(singleSquareHeight);
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.BLACK);

                // set the text to be displayed in the cell to either nothing or the target number
                Text text = new Text("");
                text.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                if (number > 0 && i == rectangleWithNumberIndex) text.setText(number + "");

                // add rectangle and text to a StackPane
                StackPane sp = new StackPane();
                sp.getChildren().addAll(rect, text);

                Pane pane = new Pane();
                pane.setId(cell.getX() + "#" + cell.getY());
                pane.getChildren().addAll(sp);

                // check if there have to be thicker borders (region borders)
                if (r.getCellRelative(cell, 0, -1) == null) {
                    // line at top
                    Line line = new Line(0, 0, singleSquareWidth, 0);
                    line.setStrokeWidth(5.0);
                    pane.getChildren().add(line);
                }
                if (r.getCellRelative(cell, 1, 0) == null) {
                    // line on right
                    Line line = new Line(singleSquareWidth, 0, singleSquareWidth, singleSquareHeight);
                    line.setStrokeWidth(5.0);
                    pane.getChildren().add(line);
                }
                if (r.getCellRelative(cell, 0, 1) == null) {
                    // line at bottom
                    Line line = new Line(0, singleSquareHeight, singleSquareWidth, singleSquareHeight);
                    line.setStrokeWidth(5.0);
                    pane.getChildren().add(line);
                }
                if (r.getCellRelative(cell, -1, 0) == null) {
                    // line to left
                    Line line = new Line(0, 0, 0, singleSquareHeight);
                    line.setStrokeWidth(4);
                    pane.getChildren().add(line);
                }

                // layout the StackPane on the board
                pane.setLayoutX(cell.getX() * singleSquareWidth);
                pane.setLayoutY(cell.getY() * singleSquareHeight);

                // add StackPane to board
                boardPane.getChildren().add(pane);
            }
        }
    }

    public void updateField(Field field) {

        // iterate over all the boardPane children, i. e. the cell panes
        for (Node node : boardPane.getChildren()) {

            // get each ID and split them into x and y coordinates
            String[] coordinates = node.getId().split("#");

            // get the current matching cell from solution
            char cellSolution = field.solution[Integer.parseInt(coordinates[1])][Integer.parseInt(coordinates[0])];

            // filter the pane's children for StackPanes (will only be one)
            ((Pane)node)
                    .getChildren()
                    .stream()
                    .filter(n -> n instanceof StackPane) // avoid the lines
                    .forEach(s -> ((StackPane) s)
                            .getChildren()
                            .forEach(c -> {
                                // switch the colours according to proposed solution
                                if (cellSolution == 'B') {
                                    if (c instanceof Rectangle) ((Rectangle) c).setFill(Color.gray(0.2));
                                    if (c instanceof Text) ((Text) c).setFill(Color.WHITE);
                                } else if (cellSolution == 'W') {
                                    if (c instanceof Rectangle) ((Rectangle) c).setFill(Color.WHITE);
                                    if (c instanceof Text) ((Text) c).setFill(Color.gray(0.2));
                                }
                            })
                    );
        }
    }

    public void setOutputText(String s) {
        outputText = new Label(s);
    }

    public void resetBoardPane() {
        this.boardPane.getChildren().clear();
    }
}
