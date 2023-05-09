package com.example.pong;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Single_Player extends Controller {
    private static final int APP_W = 500;
    private static final int APP_H = 700;

    public static final int BALL_RADIUS = 10;
    public static final int RECT_W = 100;
    public static final int RECT_H = 20;

    enum UserAction {
        NONE, LEFT, RIGHT, A, D
    }

    public static Stop_Threads RUN = new Stop_Threads();

    private static int score = 0;
    private static Text scoreText = new Text();
    private static Text finalScoreText = new Text();

    public static Scene scene;
    public static Scene playScene;
    private static Scene sceneGameOver;

    @FXML
    private static Pane root = new Pane();
    public static Stage playStage = new Stage();
    @FXML
    public static Stage singlePlayerStage = new Stage();
    private static Stage stageGameOver = new Stage();
    private static Stage restartStage = new Stage();

    public static Thread singlePlayerThread;

    @FXML
    Button singlePlayerButton = new Button();
    @FXML
    Button playButton = new Button();
    @FXML
    public static Button backToStartButtonSinglePlayer = new Button();

    public static String rectangleFile = "rectangle.mp3";
    public static Media rectangleM = new Media(new File(rectangleFile).toURI().toString());
    public static MediaPlayer rectangleSound = new MediaPlayer(rectangleM);

    @FXML
    private static Ball ball;
    @FXML
    private static Stick rectangle;

    public static UserAction action = UserAction.NONE;


    public static Parent createContent() throws IOException {
        helper();

        backToStartButtonSinglePlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    backToStartWhenButtonPressedSinglePlayer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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

        root.getChildren().addAll(scoreText, ball, rectangle, restartButton,backToStartButtonSinglePlayer,finalScoreText);
        return root;
    }

    private static void helper() {
        RUN.setRUNTrue();

        singlePlayerStage.setTitle("Singleplayer Ball Game");
        DropShadow shadow = new DropShadow();
        scoreText.setEffect(shadow);
        finalScoreText.setOpacity(0);
        finalScoreText.setFill(Color.HOTPINK);
        finalScoreText.setX(162);
        finalScoreText.setY(250);
        finalScoreText.setStyle("-fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-border-color: '#000000'; -fx-border-width: 5;");
        DropShadow dropShadow  = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        scoreText.setText("SCORE: " + score);
        scoreText.setFill(Color.BLACK);
        scoreText.setX(APP_W - 70);
        scoreText.setY(20);

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        backToStartButtonSinglePlayer.setPrefHeight(32);
        backToStartButtonSinglePlayer.setPrefWidth(35);
        restartButton.setText("");
        backToStartButtonSinglePlayer.setText("");
        backToStartButtonSinglePlayer.setEffect(dropShadow);

        restartButton.setLayoutX(192);
        restartButton.setLayoutY(270);
        backToStartButtonSinglePlayer.setLayoutX(20);
        backToStartButtonSinglePlayer.setLayoutY(20);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToStartButtonSinglePlayer.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");

        root.setPrefHeight(APP_H);
        root.setPrefWidth(APP_W);
        root.setStyle("-fx-background-image: url('https://mcdn.wallpapersafari.com/medium/99/61/CI1pFG.png')");

        ball = new Ball(BALL_RADIUS,APP_W / 2,APP_H / 2);
        rectangle = new Stick( 1,APP_W / 2.5,APP_H  - RECT_H);

        rectangle.setFill(Color.HOTPINK);
        ball.setFill(Color.CYAN);

        ball.setEffect(dropShadow);
        rectangle.setEffect(dropShadow);

        restartButton.setDisable(true);
        restartButton.setOpacity(0);

        backToStartButtonSinglePlayer.setDisable(true);
        backToStartButtonSinglePlayer.setOpacity(0);
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
            ball.setXSpeed(Math.abs(ball.xV));
            playBallSound(ballSound,ballM);
        }

        //if ball hits the right side of the window
        else if (ball.getTranslateX() + BALL_RADIUS >= APP_W){
            ball.setXSpeed(-ball.xV);
            playBallSound(ballSound,ballM);
        }

        //if ball meets the bottom of the window
        else if (ball.getTranslateY() >= APP_H - BALL_RADIUS) {
            ball.setXSpeed(0);
            ball.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);

            ball.setEffect(new BoxBlur(10,10,3));
            rectangle.setEffect(new BoxBlur(10,10,3));

            scoreText.setEffect(new BoxBlur(10,10,3));
            restartButton.setOpacity(1);
            backToStartButtonSinglePlayer.setOpacity(1);
            backToStartButtonSinglePlayer.setDisable(false);

            finalScoreText.setOpacity(1);
            finalScoreText.setStroke(Color.VIOLET);
            finalScoreText.setStrokeWidth(0.1);
            Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);
            finalScoreText.setText(" Your score : " + score);
            finalScoreText.setFont(font);
            finalScoreText.setFill(Color.HOTPINK);
            finalScoreText.setEffect(dropShadow);
        }

        //if ball meets the top of the window
        else if (ball.getTranslateY() - BALL_RADIUS <= 0){
            ball.setYSpeed(-ball.yV);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the rectangle
        else if (ball.getTranslateY() + BALL_RADIUS >= APP_H - RECT_H
                && ball.getTranslateX() + BALL_RADIUS >= rectangle.getTranslateX()
                && ball.getTranslateX() - BALL_RADIUS <= rectangle.getTranslateX() + RECT_W) {
            ++score;

            ball.setYSpeed(Math.abs(ball.yV));

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

            ball.setXSpeed(ball.xV);
            ball.setYSpeed(-ball.yV);
        }

        scoreText.setText("SCORE : " + score);
    }

    @FXML
    private static void restartGame() throws IOException {
        DropShadow shadow = new DropShadow();
        scoreText.setEffect(shadow);
        finalScoreText.setOpacity(0);

        score = 0;
        RUN.setRUNTrue();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonSinglePlayer.setDisable(true);
        backToStartButtonSinglePlayer.setOpacity(0);

        //set the elements to base position
        rectangle.setTranslateX(APP_W / 2.5);
        rectangle.setTranslateY(APP_H - RECT_H);

        //set effect
        DropShadow dropShadow  = new DropShadow();
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);
        ball.setEffect(dropShadow);
        rectangle.setEffect(dropShadow);

        //setting the initial speed for ball
        ball.setXSpeed(2);
        ball.setYSpeed(2);

        ball.setTranslateX(APP_W / 2);
        ball.setTranslateY(APP_H / 2);

        singlePlayerStage.setOpacity(1);
    }

    @FXML
    private void closeGame() {
        singlePlayerStage.close();
        restartStage.close();
        //stageGameOver.close();
    }

    public static void run() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(RUN.getRUN() == true) {
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

    public static void playBallSound(MediaPlayer music, Media media) {
        music.play();
        music.setStopTime(media.getDuration());
        music.seek(new Duration(0));
    }

    //opens a different window for game over
    private static void openGameOver() throws IOException {
        FXMLLoader fx = new FXMLLoader(Pong_Game.class.getResource("game_over.fxml"));
        sceneGameOver = new Scene(fx.load(),400,200);

        stageGameOver.setTitle("Game Over");
        stageGameOver.setResizable(false);
        stageGameOver.setScene(sceneGameOver);
        stageGameOver.show();
    }

    private static void backToStartWhenButtonPressedSinglePlayer() throws IOException {
        RUN.setRUNFalse();
        playStage.close();
        singlePlayerStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonSinglePlayer.setOpacity(0);
        backToStartButtonSinglePlayer.setDisable(true);

        play();
    }
}
