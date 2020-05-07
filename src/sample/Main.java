package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene mainScene = new Scene(root, 300, 275);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        logic.InputParser ip = new logic.InputParser();
        try {
            ip.parseFile(Path.of("C:\\Users\\Niklas\\Documents\\01_DHBW\\chocona-solver\\test\\input_data\\12"));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        launch(args);
    }
}
