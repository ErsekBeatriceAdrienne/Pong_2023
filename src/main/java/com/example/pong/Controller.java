package com.example.pong;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Controller {

    enum UserAction {
        NONE, LEFT, RIGHT
    }

    private static Scene scene;
    private static Scene playScene;
    private static Scene restartScene;
    private static Scene sceneGameOver;
    private static Scene multiplayerScene;

    @FXML
    private static AnchorPane pane = new AnchorPane();
    @FXML
    private static Pane root = new Pane();

    private static Stage playStage = new Stage();
    @FXML
    private static Stage gameStage = new Stage();
    private static Stage stageGameOver = new Stage();
    private static Stage restartStage = new Stage();
    private static Stage multiplayerStage = new Stage();

    private static Thread gameThread;

    @FXML
    Button singlePlayerButton = new Button();
    @FXML
    Button playButton = new Button();
    @FXML
    private Button yesButton = new Button();
    @FXML
    private Button noButton = new Button();
    @FXML
    private static Button restartButton = new Button();
    @FXML
    private static Button multiplayerButton = new Button();
    private static Parent parent;

    private static final int APP_W = 500;
    private static final int APP_H = 700;

    private static final int BALL_RADIUS = 10;
    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    private static String oufFile = "rect.mp3";
    private static String ballFile = "ball_sound.mp3";

    private static Media oufM = new Media(new File(oufFile).toURI().toString());
    private static Media ballM = new Media(new File(ballFile).toURI().toString());

    private static MediaPlayer oufSound = new MediaPlayer(oufM);
    private static MediaPlayer ballSound = new MediaPlayer(ballM);

    @FXML
    private static Ball ball;
    @FXML
    private static Stick rectangle;

    private static UserAction action = UserAction.NONE;


    @FXML
    public Stage play() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("start-the-game.fxml"));
        playScene = new Scene(fxmlLoader.load(),500,450);

        playStage.setTitle("Ball Game");
        playStage.setResizable(false);
        playStage.setOpacity(0.8);
        playStage.setScene(playScene);
        //playStage.getIcons().add(new Image(getClass().getResourceAsStream("/Users/ersekbeatrice-adrienne/Desktop/Sapientia/Labdás játék/Pong_Game_2023_ujbol/src/main/resources/com/example/pong/style/game_icon.jpg")));

        playStage.show();
        return playStage;
    }

    public static Parent createContent() throws IOException {
        playStage.close();

        root.setPrefHeight(APP_H);
        root.setPrefWidth(APP_W);
        root.setBackground(Background.fill(Color.BLACK));

        ball = new Ball(BALL_RADIUS);
        rectangle = new Stick( 1);

        rectangle.setFill(Color.WHITE);
        ball.setFill(Color.CYAN);

        root.getChildren().addAll(ball, rectangle);
        return root;
    }

    public static void collisionCheck() throws IOException {
        //stops rectangle at edge of the window and moves rectangle
        switch (action) {
            case LEFT:
                if (rectangle.getTranslateX() - rectangle.getSpeed() >= 0) {
                    rectangle.setTranslateX(rectangle.getTranslateX() - rectangle.getSpeed());
                }
                break;
            case RIGHT:
                if (rectangle.getTranslateX() + RECT_W + rectangle.getSpeed() <= APP_W) {
                    rectangle.setTranslateX(rectangle.getTranslateX() + rectangle.getSpeed());
                }
                break;
            case NONE:
                break;
        }

        //stops ball to get outside of window
        //if ball meets the left side of the window
        if (ball.getTranslateX() - BALL_RADIUS <= 0){
            ball.setX(Math.abs(ball.xV));
            playBallSound(ballSound,ballM);
        }

        //if ball hits the right side of the window
        else if (ball.getTranslateX() + BALL_RADIUS >= APP_W){
            ball.setX(-ball.xV);
            playBallSound(ballSound,ballM);
        }

        //if ball meets the bottom of the window
        else if (ball.getTranslateY() + BALL_RADIUS >= APP_H) {
            ball.xV = ball.getX();
            ball.yV = APP_H;
            //gameThread.interrupt();
            playBallSound(oufSound,oufM);
            stopGame();
        }

        //if ball meets the top of the window
        else if (ball.getTranslateY() - BALL_RADIUS <= 0){
            ball.setY(-ball.yV);
            playBallSound(ballSound,ballM);
        }

        //if ball meets the rectangle
        else if (ball.getTranslateY() + BALL_RADIUS >= APP_H - RECT_H
                && ball.getTranslateX() + BALL_RADIUS >= rectangle.getTranslateX()
                && ball.getTranslateX() - BALL_RADIUS <= rectangle.getTranslateX() + RECT_W) {
            ball.setY(Math.abs(ball.yV));

            playBallSound(ballSound,ballM);

            //optional for more difficulty
            ball.yV++;
            if (ball.xV > 0) {
                //optional for more difficulty
                ++ball.xV;
            }
            else {
                --ball.xV;
            }

            ball.setX(ball.xV);
            ball.setY(-ball.yV);
        }
    }

    @FXML
    public void startTheGame() throws IOException {
        playStage.close();

        scene = new Scene(createContent());
        gameThread = new Thread(Controller::run);
        gameThread.start();

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

        gameStage.setTitle("Ball Game");
        gameStage.setOpacity(1);
        gameStage.setScene(scene);
        gameStage.setResizable(false);
        gameStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        gameStage.show();
    }

    @FXML
    private void restartGame() throws IOException {
        //set the elements to base position
        rectangle.setTranslateX(APP_W / 2.5);
        rectangle.setTranslateY(APP_H - RECT_H);

        ball.setCenterX(APP_W / 2);
        ball.setCenterY(APP_H / 2);

        //gameThread.setDaemon(false);
        //gameThread.start();

        gameStage.setOpacity(1);
        gameStage.setScene(scene);
        gameStage.show();
    }

    /*private static void stopGame() throws IOException {
        gameThread.setDaemon(false);
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("restartWindow.fxml"));
        restartScene = new Scene(fxmlLoader.load(),500,700);

        gameStage.setScene(restartScene);
        gameStage.setOpacity(0.8);
        restartButton.setOpacity(1);

        gameStage.show();
        //gameThread.setDaemon(true);
    }*/

    private static void stopGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("restartWindow.fxml"));
        restartScene = new Scene(fxmlLoader.load(),500,700);

        restartStage.setScene(restartScene);
        restartStage.setOpacity(0.8);
        restartButton.setOpacity(1);

        restartStage.show();
    }

    @FXML
    private void closeGame() {
        gameStage.close();
        restartStage.close();
        stageGameOver.close();
    }

    private static void openGameOver() throws IOException {
        FXMLLoader fx = new FXMLLoader(Pong_Game.class.getResource("game_over.fxml"));
        sceneGameOver = new Scene(fx.load(),400,200);

        stageGameOver.setTitle("Game Over");
        stageGameOver.setResizable(false);
        stageGameOver.setScene(sceneGameOver);
        stageGameOver.show();
    }

    private static void run() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1) {
                ball.moveBall();
                rectangle.moveRectangle();
                //smooths out the animation
                Toolkit.getDefaultToolkit().sync();
                try {
                    collisionCheck();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                delta--;
            }
        }
    }

    private static void playBallSound(MediaPlayer music, Media media) {
        music.play();
        music.setStopTime(media.getDuration());
        music.seek(new Duration(0));
    }


    ///MULTIPLAYER

    @FXML
    private void startMultiplayer() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("multiplayer.fxml"));
        multiplayerScene = new Scene(fxmlLoader.load(),800,700);

        multiplayerStage.setTitle("Ball Game");
        multiplayerStage.setResizable(false);
        multiplayerStage.setScene(multiplayerScene);
        multiplayerStage.show();
    }
}
