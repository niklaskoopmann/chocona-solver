package gui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import structure.Cell;
import structure.Field;
import structure.Region;

public class Controller {

    @FXML
    private AnchorPane boardPane;

    public void drawField(Field field) {

        double singleSquareWidth = boardPane.getPrefWidth() / field.getWidth();
        double singleSquareHeight = boardPane.getPrefHeight() / field.getHeight();

        for (Region r : field.getRegions()) {

            for (Cell c : r.cells) {

                Rectangle rect = new Rectangle();
                rect.setWidth(singleSquareWidth);
                rect.setHeight(singleSquareHeight);
                rect.setFill(Paint.valueOf("blue"));

                boardPane.getChildren().add(rect);

                rect.setX(c.getX() * singleSquareWidth);
                rect.setY(c.getY() * singleSquareHeight);
            }
        }
    }

    public void dummy() {
        System.out.println("Dummy called!");
    }
}
