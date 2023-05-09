package com.example.pong.game_modes;

import com.example.pong.Controller;
import com.example.pong.obejcts.Stop_Threads;
import com.example.pong.interfaces.IMode;
import com.example.pong.obejcts.Ball;
import com.example.pong.obejcts.Stick;
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
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.example.pong.game_modes.Single_Player.*;

public class Multi_Player implements IMode {
    @FXML
    public static Button restartButton = new Button();

    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);

    private static final int APP_W = 500;
    private static final int APP_H = 700;
    private static final int MULTI_W = 800;
    private static final int MULTI_H = 700;

    public static Stop_Threads RUN2 = new Stop_Threads();
    public static int score1 = 0;
    public static int score2 = 0;
    private static Text scoreMulti1 = new Text();
    private static Text scoreMulti2 = new Text();
    private static Text winnerText = new Text();
    public static Scene multiplayerScene;
    @FXML
    private static Pane multiplayerPane = new Pane();
    public static Stage multiplayerStage = new Stage();
    public static Thread multiPlayerThread;
    @FXML
    private static Button backToStartButtonMultiPlayer = new Button();
    @FXML
    private static Line separator = new Line();

    //objects
    private static Ball player1Ball;
    private static Ball player2Ball;
    private static Stick player1Stick;
    private static Stick player2Stick;

    public static void startMultiplayer() throws IOException {
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
        multiplayerStage.getIcons().add(new Image(Multi_Player.class.getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        multiplayerStage.show();
    }

    public static Parent createMultiplayer() throws IOException {
        initialize();

        backToStartButtonMultiPlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    backToStartWhenButtonPressedMultiPlayer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    restartMultiplayer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        multiplayerPane.getChildren().addAll(winnerText,scoreMulti1,scoreMulti2,player1Ball,player2Ball,player1Stick,player2Stick,separator,restartButton,backToStartButtonMultiPlayer);
        return multiplayerPane;
    }

    private static void initialize() {
        DropShadow dropShadow  = new DropShadow();
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);
        winnerText.setOpacity(0);
        winnerText.setFill(Color.HOTPINK);
        winnerText.setStroke(Color.BLACK);
        winnerText.setStrokeWidth(0.5);
        winnerText.setFont(font);
        winnerText.setEffect(dropShadow);
        multiplayerStage.setTitle("Multiplayer Ball Game");

        DropShadow shadow = new DropShadow();
        scoreMulti1.setEffect(shadow);
        scoreMulti2.setEffect(shadow);
        scoreMulti1.setText("SCORE: " + score1);
        scoreMulti1.setX(10);
        scoreMulti1.setY(20);
        scoreMulti2.setText("SCORE: " + score2);
        scoreMulti2.setX(MULTI_W - 70);
        scoreMulti2.setY(20);

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(343);
        restartButton.setLayoutY(279);

        backToStartButtonMultiPlayer.setEffect(dropShadow);
        backToStartButtonMultiPlayer.setPrefHeight(32);
        backToStartButtonMultiPlayer.setPrefWidth(32);

        backToStartButtonMultiPlayer.setLayoutX(20);
        backToStartButtonMultiPlayer.setLayoutY(30);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToStartButtonMultiPlayer.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");

        player1Ball = new Ball(BALL_RADIUS,MULTI_W / 4,MULTI_H / 2 + MULTI_H / 4);
        player2Ball = new Ball(BALL_RADIUS,(MULTI_W/2) + MULTI_W / 4,MULTI_H / 2 + MULTI_H / 6);

        player1Ball.setFill(Color.HOTPINK);
        player2Ball.setFill(Color.GREENYELLOW);

        player1Stick = new Stick(1,MULTI_W / 4,MULTI_H - RECT_H);
        player2Stick = new Stick(2,MULTI_W / 2 + APP_W / 2,MULTI_H - RECT_H);

        player1Ball.setEffect(dropShadow);
        player2Ball.setEffect(dropShadow);
        player1Stick.setEffect(dropShadow);
        player2Stick.setEffect(dropShadow);

        player1Stick.setFill(Color.CYAN);
        player2Stick.setFill(Color.HOTPINK);

        separator.setStroke(Color.BLACK);
        separator.setLayoutX(325);
        separator.setLayoutY(0);
        separator.setStartX(75);
        separator.setStartY(-14);
        separator.setEndX(75);
        separator.setEndY(700);
        separator.setScaleX(1);
        separator.setStartY(1);
        separator.setScaleZ(1);

        restartButton.setEffect(dropShadow);
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonMultiPlayer.setDisable(true);
        backToStartButtonMultiPlayer.setOpacity(0);

        multiplayerPane.setPrefHeight(MULTI_H);
        multiplayerPane.setPrefWidth(MULTI_W);
        multiplayerPane.setStyle("-fx-background-image: url('https://cdn.shopify.com/s/files/1/0575/0987/1774/files/1_6df15c2e-7475-4b2e-839e-3826bc5c02f6.png?v=1653967370')");
        //multiplayerPane.setBackground(Background.fill(Color.GREY));
    }

    public static void runMultiplayer() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(RUN2.getRUN() == true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1) {
                player1Ball.moveBall();
                player2Ball.moveBall();
                moveRectangles();
                //smooths out the animation
                Toolkit.getDefaultToolkit().sync();
                try {
                    collisionCheckMultiplayer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                delta--;
            }
        }
    }

    private static void moveRectangles() {
        player1Stick.moveRectangle();
        player2Stick.moveRectangle();
    }

    private static void collisionCheckMultiplayer() throws IOException{
        switch (action) {
            case LEFT:
                if (player2Stick.getTranslateX() - player2Stick.getSpeed() >= MULTI_W / 2) {
                    player2Stick.setTranslateX(player2Stick.getTranslateX() - player2Stick.getSpeed());
                }
                break;
            case A:
                if (player1Stick.getTranslateX() - player1Stick.getSpeed() >= 0) {
                    player1Stick.setTranslateX(player1Stick.getTranslateX() - player1Stick.getSpeed());
                }
                break;
            case RIGHT:
                if (player2Stick.getTranslateX() + RECT_W + player2Stick.getSpeed() <= MULTI_W) {
                    player2Stick.setTranslateX(player2Stick.getTranslateX() + player2Stick.getSpeed());
                }
                break;
            case D:
                if (player1Stick.getTranslateX() + RECT_W + player1Stick.getSpeed() <= MULTI_W / 2) {
                    player1Stick.setTranslateX(player1Stick.getTranslateX() + player1Stick.getSpeed());
                }
                break;
            case NONE:
                break;
        }

        collisionBall1();
        collisionBall2();
        //if ball meets the bottom of the window
        if (player1Ball.getTranslateY() >= MULTI_H - BALL_RADIUS || player2Ball.getTranslateY() >= MULTI_H - BALL_RADIUS) {
            player1Ball.setXSpeed(0);
            player1Ball.setYSpeed(0);
            player2Ball.setXSpeed(0);
            player2Ball.setYSpeed(0);

            scoreMulti1.setEffect(new BoxBlur(10,10,3));
            scoreMulti2.setEffect(new BoxBlur(10,10,3));
            restartButton.setDisable(false);
            backToStartButtonMultiPlayer.setDisable(false);

            player1Ball.setEffect(new BoxBlur(10,10,3));
            player1Stick.setEffect(new BoxBlur(10,10,3));
            player2Ball.setEffect(new BoxBlur(10,10,3));
            player2Stick.setEffect(new BoxBlur(10,10,3));
            //multiplayerPane.setEffect(new GaussianBlur());

            DropShadow dropShadow  = new DropShadow();
            dropShadow = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);

            restartButton.setOpacity(1);
            backToStartButtonMultiPlayer.setOpacity(1);
        }
    }

    private static void collisionBall1() {
        //stops when balls hit the left side
        if (player1Ball.getTranslateX() - BALL_RADIUS <= 0) {
            player1Ball.setXSpeed(Math.abs(player1Ball.xV));
            playBallSound(ballSound,ballM);
        }
        //stops when balls hit the right side
        else if (player1Ball.getTranslateX() + BALL_RADIUS >= MULTI_W / 2) {
            player1Ball.setXSpeed(-player1Ball.xV);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the top of the window
        else if (player1Ball.getTranslateY() - BALL_RADIUS <= 0){
            player1Ball.setYSpeed(-player1Ball.yV);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the bottom of the window
        else if (player1Ball.getTranslateY() >= MULTI_H - BALL_RADIUS) {
            winnerText.setOpacity(100);
            if (score1 < score2) {
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS PLAYER 2!");
            }
            else if (score1 > score2){
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS PLAYER 1!");
            }
            else if (score1 == score2) {
                winnerText.setX(APP_W / 2 + 50);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("NO WINNER :) !");
            }
            player1Ball.setXSpeed(0);
            player1Ball.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);

            player1Ball.setEffect(new BoxBlur(10,10,3));

            restartButton.setOpacity(1);
            backToStartButtonSinglePlayer.setOpacity(1);
            backToStartButtonSinglePlayer.setDisable(false);
        }
        //if ball meets the rectangle
        else if (player1Ball.getTranslateY() + BALL_RADIUS >= MULTI_H - RECT_H
                && player1Ball.getTranslateX() + BALL_RADIUS >= player1Stick.getTranslateX()
                && player1Ball.getTranslateX() - BALL_RADIUS <= player1Stick.getTranslateX() + RECT_W) {
            player1Ball.setYSpeed(Math.abs(player1Ball.yV));
            ++score1;

            playBallSound(ballSound,ballM);

            //optional for more difficulty
            player1Ball.yV++;
            if (player1Ball.xV > 0) {
                //optional for more difficulty
                ++player1Ball.xV;
            }
            else {
                --player1Ball.xV;
            }

            player1Ball.setXSpeed(player1Ball.xV);
            player1Ball.setYSpeed(-player1Ball.yV);
        }
        scoreMulti1.setText("SCORE : " + score1);
    }

    private static void collisionBall2() {
        //stops when balls hit the left side
        if (player2Ball.getTranslateX() - BALL_RADIUS <= MULTI_W / 2) {
            player2Ball.setXSpeed(Math.abs(player2Ball.xV));
            playBallSound(ballSound, ballM);
        }
        //stops when balls hit the right side
        else if (player2Ball.getTranslateX() + BALL_RADIUS >= MULTI_W) {
            player2Ball.setXSpeed(-player2Ball.xV);
            playBallSound(ballSound, ballM);
        }
        //if ball meets the top of the window
        else if (player2Ball.getTranslateY() - BALL_RADIUS <= 0) {
            player2Ball.setYSpeed(-player2Ball.yV);
            playBallSound(ballSound, ballM);
        }
        //if ball meets the bottom of the window
        else if (player2Ball.getTranslateY() >= MULTI_H - BALL_RADIUS) {
            winnerText.setOpacity(100);
            if (score1 < score2) {
                winnerText.setX(APP_W / 2 + 30);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS PLAYER 2!");
            }
            else if (score1 > score2){
                winnerText.setX(APP_W / 2 + 30);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS PLAYER 1!");
            }
            else if (score1 == score2) {
                winnerText.setX(APP_W / 2 + 50);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("NO WINNER :) !");
            }

            scoreMulti1.setEffect(new BoxBlur(10,10,3));
            scoreMulti2.setEffect(new BoxBlur(10,10,3));

            player2Ball.setXSpeed(0);
            player2Ball.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);

            player2Ball.setEffect(new BoxBlur(10, 10, 3));

            restartButton.setOpacity(1);
            backToStartButtonSinglePlayer.setOpacity(1);
            backToStartButtonSinglePlayer.setDisable(false);
        }
        //if ball meets the rectangle
        else if (player2Ball.getTranslateY() + BALL_RADIUS >= APP_H - RECT_H
                && player2Ball.getTranslateX() + BALL_RADIUS >= player2Stick.getTranslateX()
                && player2Ball.getTranslateX() - BALL_RADIUS <= player2Stick.getTranslateX() + RECT_W) {
            player2Ball.setYSpeed(Math.abs(player2Ball.yV));
            ++score2;

            playBallSound(ballSound, ballM);

            //optional for more difficulty
            player2Ball.yV++;
            if (player2Ball.xV > 0) {
                //optional for more difficulty
                ++player2Ball.xV;
            } else {
                --player2Ball.xV;
            }

            player2Ball.setXSpeed(player2Ball.xV);
            player2Ball.setYSpeed(-player2Ball.yV);
        }
        scoreMulti2.setText("SCORE : " + score2);
    }

    private static void restartMultiplayer() throws IOException {
        winnerText.setOpacity(0);
        score1 = 0;
        score2 = 0;
        RUN2.setRUNTrue();
        restartButton.setOpacity(0);
        restartButton.setDisable(true);

        DropShadow dropShadow  = new DropShadow();
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);
        scoreMulti1.setEffect(dropShadow);
        scoreMulti2.setEffect(dropShadow);
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonMultiPlayer.setDisable(true);
        backToStartButtonMultiPlayer.setOpacity(0);

        //set the elements to base position
        player1Stick.setTranslateX(MULTI_W / 4);
        player1Stick.setTranslateY(MULTI_H - RECT_H);
        player2Stick.setTranslateX(MULTI_W / 2 + APP_W / 2);
        player2Stick.setTranslateY(MULTI_H - RECT_H);

        player1Ball.setTranslateX(MULTI_W / 4);
        player1Ball.setTranslateY(MULTI_H / 2 + MULTI_H / 4);
        player2Ball.setTranslateX((MULTI_W/2) + MULTI_W / 4);
        player2Ball.setTranslateY(MULTI_H / 2 - MULTI_H / 6);

        //set effect
        player1Ball.setEffect(dropShadow);
        player2Ball.setEffect(dropShadow);
        player1Stick.setEffect(dropShadow);
        player2Stick.setEffect(dropShadow);

        multiplayerPane.setEffect(dropShadow);

        //setting the initial speed for ball
        player1Ball.setXSpeed(2);
        player1Ball.setYSpeed(2);
        player2Ball.setXSpeed(2);
        player2Ball.setYSpeed(2);

        multiplayerStage.setOpacity(1);
    }

    private static void backToStartWhenButtonPressedMultiPlayer() throws IOException {
        RUN2.setRUNFalse();
        multiplayerStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonMultiPlayer.setOpacity(0);
        backToStartButtonMultiPlayer.setDisable(true);

        Controller.play();
    }
}
