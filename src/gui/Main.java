package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import structure.Field;

import java.nio.file.Path;

public class Main extends Application {

    private static Field testField;

    public static void main(String[] args) {

        logic.InputParser ip = new logic.InputParser();
        try {
            ip.parseFile(Path.of("C:\\Users\\Niklas\\Documents\\01_DHBW\\chocona-solver\\test\\input_data\\12"));
            Field f = ip.parseToField(ip.getJsonObj());
            f.getRegions().forEach(r -> System.out.println(r.toString()));
            testField = f;
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();
        Scene mainScene = new Scene(root, 800, 600);

        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode == KeyCode.F5) {
                controller.drawField(testField);
                System.out.println("F5 pressed!");
            } else if(keyCode == KeyCode.F6) {
                System.out.println("F6 pressed!");
            } else if(keyCode == KeyCode.F8) {
                System.out.println("F8 pressed!");
            }
            keyEvent.consume();
        };

        mainScene.setOnKeyReleased(keyEventHandler);


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
