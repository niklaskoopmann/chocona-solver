package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import structure.Field;

import java.nio.file.Path;

public class Main extends Application {

    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        controller = loader.getController();
        Parent root = loader.load();
        Scene mainScene = new Scene(root, 800, 600);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        logic.InputParser ip = new logic.InputParser();
        try {
            ip.parseFile(Path.of("C:\\Users\\Niklas\\Documents\\01_DHBW\\chocona-solver\\test\\input_data\\12"));
            Field f = ip.parseToField(ip.getJsonObj());
            f.getRegions().forEach(r -> System.out.println(r.toString()));
            controller.drawField(f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }
}
