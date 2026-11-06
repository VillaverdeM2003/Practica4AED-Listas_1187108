package org.example.practica4;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        EightOffGUI gui = new EightOffGUI();
        gui.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}