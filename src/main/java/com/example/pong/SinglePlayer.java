package com.example.pong;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SinglePlayer {
    enum UserAction {
        NONE, LEFT, RIGHT, A, D
    }
    private static Controller.UserAction action = Controller.UserAction.NONE;
    private static final int APP_W = 500;
    private static final int APP_H = 700;
    private static final int BALL_RADIUS = 10;
    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    private static int score = 0;
    private static Text scoreText = new Text();
    private static Scene scene;
    private static DropShadow dropShadow;

    @FXML
    private static Pane root = new Pane();
    @FXML
    private static Stage singlePlayerStage = new Stage();
    private static Stage restartStage = new Stage();
    @FXML
    Button singlePlayerButton = new Button();
    @FXML
    private static Button restartButton = new Button();
    @FXML
    private static Button backToStartButtonSinglePlayer = new Button();

    private static String ballFile = "ball_sound.mp3";
    private static Media ballM = new Media(new File(ballFile).toURI().toString());
    private static MediaPlayer ballSound = new MediaPlayer(ballM);
    @FXML
    private static Ball ball;
    @FXML
    private static Stick rectangle;


}
