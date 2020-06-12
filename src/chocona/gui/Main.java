package chocona.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.stage.Stage;
import chocona.logic.InputParser;
import chocona.logic.Solver;
import chocona.structure.Field;

import java.nio.file.Path;

public class Main extends Application {

    private static Field testField;
    private static Solver solver;
    private static int counter;

    public static void main(String[] args) {

        counter = 0;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();
        Scene mainScene = new Scene(root, 800, 600);
        mainScene.setOnDragOver(event -> {
            if (event.getGestureSource() != mainScene && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        mainScene.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean dropSuccess = false;
            controller.resetBoardPane();
            counter = 0;
            if (db.hasFiles()) {
                InputParser ip = new InputParser();
                try {
                    ip.parseFile(Path.of(db.getFiles().get(0).getAbsolutePath()));
                    Field f = ip.parseToField(ip.getJsonObject());
                    f.getRegions().forEach(r -> System.out.println(r.toString()));
                    testField = f;
                    controller.drawField(testField);
                    solver = new Solver(testField);
                    dropSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dragEvent.setDropCompleted(dropSuccess);
                dragEvent.consume();
            }
        });

        // detect keystrokes
        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode == KeyCode.F5) {
                if (solver != null) {
                    solver.stepGeneration(counter, solver.getPopulation());
                    counter++;
                    controller.updateField(solver.getCurrentBestPlayerSolution());
                    if (solver.isSolutionFound()) controller.setOutputText("Solution found by generation " + counter + ".");
                    else controller.setOutputText("Generation " + counter + ", solution not found yet.");

                }
            } else if(keyCode == KeyCode.F6) {
                if(solver != null) {
                    Field solvedTestField = solver.solvePuzzleGenetic();
                    controller.updateField(solvedTestField);
                    counter = solver.getCurrentGenerationIndex();
                    if (solver.isSolutionFound()) controller.setOutputText("Solution found by generation " + counter + ".");
                    else controller.setOutputText("Generation " + counter + ", solution not found yet.");
                }
            } else if(keyCode == KeyCode.F8) {
                System.exit(0);
            }
            keyEvent.consume();
        };

        mainScene.setOnKeyReleased(keyEventHandler);


        primaryStage.setTitle("Chocona Solver");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
