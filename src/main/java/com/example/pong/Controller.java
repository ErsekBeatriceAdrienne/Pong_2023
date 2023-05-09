package com.example.pong;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.example.pong.Multi_Player.*;
import static com.example.pong.Single_Player.*;
import static com.example.pong.Player_Vs_Player.*;
import static com.example.pong.Player_Vs_Computer.*;

public class Controller {
    @FXML
    public static Button restartButton = new Button();

    public static final int BALL_RADIUS = 10;
    public static final int RECT_W = 100;
    public static final int RECT_H = 20;

    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);

    public static Stage play() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("start-the-game.fxml"));
        playScene = new Scene(fxmlLoader.load(),500,450);

        playStage.setTitle("Ball Game");
        playStage.setResizable(false);
        playStage.setOpacity(0.8);
        playStage.setScene(playScene);


        playStage.show();
        return playStage;
    }


    ///SINGLE_PLAYER
    @FXML
    public void startTheGame() throws IOException {
        playStage.close();
        RUN.setRUNTrue();
        scene = new Scene(createContent());
        singlePlayerThread = new Thread(Single_Player::run);
        singlePlayerThread.start();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                case A:
                    action = UserAction.LEFT;
                    break;
                case RIGHT:
                case D:
                    action = UserAction.RIGHT;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                case A:
                case RIGHT:
                case D:
                    action = UserAction.NONE;
                    break;
            }
        });

        singlePlayerStage.setTitle("Ball Game");
        singlePlayerStage.setOpacity(1);
        singlePlayerStage.setScene(scene);
        singlePlayerStage.setResizable(false);
        singlePlayerStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        singlePlayerStage.show();
    }

    ///MULTIPLAYER
    @FXML
    private void startMultiplayer() throws IOException {
        playStage.close();
        RUN2.setRUNTrue();
        multiplayerScene = new Scene(createMultiplayer());
        multiPlayerThread = new Thread(Multi_Player::runMultiplayer);
        multiPlayerThread.start();

        multiplayerScene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case LEFT:
                    action = UserAction.LEFT;
                    break;
                case A:
                    action = UserAction.A;
                    break;
                case D:
                    action = UserAction.D;
                    break;
                case RIGHT:
                    action = UserAction.RIGHT;
                    break;
            }
        });

        multiplayerScene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case LEFT:
                case A:
                case RIGHT:
                case D:
                    action = UserAction.NONE;
                    break;
            }
        });

        multiplayerStage.setScene(multiplayerScene);
        multiplayerStage.setTitle("Ball Game");
        multiplayerStage.setOpacity(1);
        multiplayerStage.setResizable(false);
        multiplayerStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        multiplayerStage.show();
    }

    ///PING PONG PVP
    @FXML
    private void startPVP() throws IOException {
        playStage.close();
        RUN_PVP.setRUNTrue();
        pvpScene = new Scene(createContentPVP());
        pvpThread = new Thread(Player_Vs_Player::runPVP);
        pvpThread.start();

        pvpScene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                    actionPVP = UserActionPVP.UP;
                    break;
                case W:
                    actionPVP = UserActionPVP.W;
                    break;
                case S:
                    actionPVP = UserActionPVP.S;
                    break;
                case DOWN:
                    actionPVP = UserActionPVP.DOWN;
                    break;
            }
        });

        pvpScene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                case S:
                case DOWN:
                    actionPVP = UserActionPVP.NONE;
                    break;
            }
        });

        pvpStage.setScene(pvpScene);
        pvpStage.setTitle("Ping Pong PvP");
        pvpStage.setOpacity(1);
        pvpStage.setResizable(false);
        pvpStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        pvpStage.show();
    }

    ///PLAYER VS COMPUTER
    @FXML
    private void start_PVC() throws IOException {
        playStage.close();
        RUN_PVC.setRUNTrue();
        pvcScene = new Scene(createContent_PVC());
        pvcThread = new Thread(Player_Vs_Computer::run_PVC);
        pvcThread.start();

        pvcScene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                    actionPVC = UserActionPVC.UP;
                    break;
                case W:
                    actionPVC = UserActionPVC.W;
                    break;
                case S:
                    actionPVC = UserActionPVC.S;
                    break;
                case DOWN:
                    actionPVC = UserActionPVC.DOWN;
                    break;
            }
        });

        pvcScene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                case S:
                case DOWN:
                    actionPVC = UserActionPVC.NONE;
                    break;
            }
        });

        pvcStage.setScene(pvcScene);
        pvcStage.setTitle("Ping Pong PvP");
        pvcStage.setOpacity(1);
        pvcStage.setResizable(false);
        pvcStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        pvcStage.show();
    }
}


