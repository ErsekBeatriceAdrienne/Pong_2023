package com.example.pong;

import javafx.application.Application;
import javafx.stage.Stage;

public class Pong_Game extends Application {
    Controller controller = new Controller();

    @Override
    public void start(Stage stage) throws Exception {
        controller.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}