package com.example.pong;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.MotionBlur;
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

    private static DropShadow dropShadow;

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
    private static Button restartButton = new Button();
    @FXML
    private static Button multiplayerButton = new Button();
    private static Parent parent;

    private static final int APP_W = 500;
    private static final int APP_H = 700;

    private static final int BALL_RADIUS = 10;
    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    private static String ballFile = "ball_sound.mp3";
    private static Media ballM = new Media(new File(ballFile).toURI().toString());
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

        playStage.show();
        return playStage;
    }

    public static Parent createContent() throws IOException {
        playStage.close();

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(192);
        restartButton.setLayoutY(270);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");

        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    restartGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        root.setPrefHeight(APP_H);
        root.setPrefWidth(APP_W);
        root.setStyle("-fx-background-image: url('https://i.pinimg.com/564x/6f/bc/ef/6fbcefcc86203d915581a8ea1c68f00c.jpg')");

        ball = new Ball(BALL_RADIUS);
        rectangle = new Stick( 1);

        rectangle.setFill(Color.HOTPINK);
        ball.setFill(Color.CYAN);

        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        ball.setEffect(dropShadow);
        rectangle.setEffect(dropShadow);

        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        root.getChildren().addAll(ball, rectangle, restartButton);
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
        else if (ball.getTranslateY() >= APP_H - BALL_RADIUS) {
            ball.setX(0);
            ball.setY(0);
            restartButton.setDisable(false);
            restartButton.setEffect(dropShadow);

            ball.setEffect(new BoxBlur(10,10,3));
            rectangle.setEffect(new BoxBlur(10,10,3));

            restartButton.setOpacity(1);
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
    private static void restartGame() throws IOException {
        restartButton.setDisable(true);
        restartButton.setOpacity(0);

        //set the elements to base position
        rectangle.setTranslateX(APP_W / 2.5);
        rectangle.setTranslateY(APP_H - RECT_H);

        //set effect
        ball.setEffect(dropShadow);
        rectangle.setEffect(dropShadow);

        //setting the initial speed for ball
        ball.setX(2);
        ball.setY(2);

        ball.setTranslateX(APP_W / 2);
        ball.setTranslateY(APP_H / 2);

        gameStage.setOpacity(1);
    }

    private static void stopGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Pong_Game.class.getResource("restartWindow.fxml"));
        restartScene = new Scene(fxmlLoader.load(),500,700);

        restartStage.setScene(restartScene);
        restartStage.setOpacity(0.8);
        restartButton.setOpacity(1);

        restartStage.show();
        restartStage.close();
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
