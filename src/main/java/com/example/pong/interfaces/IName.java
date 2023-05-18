package com.example.pong.interfaces;
import javafx.stage.Stage;

import java.io.IOException;

public interface IName {
    final int HEIGHT = 300;
    final int WIDTH = 400;

    void start(Stage stage) throws IOException;
    void save_names();
    void clear();
}
