package com.example.pong;

import com.example.pong.obejcts.Stop_Threads;
import com.example.pong.interfaces.IMode;
import com.example.pong.obejcts.Ball;
import com.example.pong.obejcts.Stick;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.pong.Controller.*;
import static com.example.pong.Multi_Player.score1;
import static com.example.pong.Multi_Player.score2;
import static com.example.pong.Single_Player.playBallSound;
import static com.example.pong.Single_Player.playStage;

public class Player_Vs_Player implements IMode {

    private static Random random = new Random();

    private static int randomColorGenerator;
    private static ArrayList<Color> colorsOfTheBall = new ArrayList<>();
    private static FillTransition transitionOfBall;

    @FXML
    public static Button restartButton = new Button();

    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);
    enum UserActionPVP {
        NONE, UP, DOWN, W, S
    }
    public static UserActionPVP actionPVP = UserActionPVP.NONE;

    public static Stop_Threads RUN_PVP = new Stop_Threads();
    public static Scene pvpScene;
    public static Stage pvpStage = new Stage();
    @FXML
    private static Button backToMainPVP = new Button();

    public static final int BALL_RADIUS = 10;
    public static final int RECT_W = 100;
    public static final int RECT_H = 20;
    private static final int PVP_W = 1000;
    private static final int PVP_H = 600;

    private static int score1_PVP = 0;
    private static int score2_PVP = 0;
    private static Text scorePVP1 = new Text();
    private static Text scorePVP2 = new Text();
    private static Text winnerPVPText = new Text();
    @FXML
    private static Pane pvpPane = new Pane();
    public static Thread pvpThread;

    //objects
    private static Ball pvpBall;
    private static Stick pvpRectangle1;
    private static Stick pvpRectangle2;

    public static void startPVP() throws IOException {
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
        pvpStage.getIcons().add(new Image(Player_Vs_Player.class.getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        pvpStage.show();
    }

    public static Parent createContentPVP() throws IOException {
        pvpInitializer();
        backToMainPVP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    backToStartWhenButtonPressedPVP();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    restart_PVP();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pvpPane.getChildren().addAll(scorePVP1,scorePVP2, pvpBall, pvpRectangle1,pvpRectangle2, restartButton,backToMainPVP,winnerPVPText);
        return pvpPane;
    }

    private static void pvpInitializer() {

        colorsOfTheBall.add(Color.LIGHTBLUE);
        colorsOfTheBall.add(Color.LIGHTCYAN);
        colorsOfTheBall.add(Color.LIGHTPINK);
        colorsOfTheBall.add(Color.LIGHTSALMON);
        colorsOfTheBall.add(Color.LIGHTGREEN);
        colorsOfTheBall.add(Color.LIGHTGRAY);
        colorsOfTheBall.add(Color.CHARTREUSE);
        colorsOfTheBall.add(Color.BLUEVIOLET);
        colorsOfTheBall.add(Color.HOTPINK);
        colorsOfTheBall.add(Color.CYAN);

        score1_PVP = 0;
        score2_PVP = 0;
        RUN_PVP.setRUNTrue();
        DropShadow dropShadow  = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);
        winnerPVPText.setOpacity(0);
        winnerPVPText.setFill(Color.HOTPINK);
        winnerPVPText.setStroke(Color.BLACK);
        winnerPVPText.setStrokeWidth(0.5);
        winnerPVPText.setFont(font);
        winnerPVPText.setEffect(dropShadow);
        winnerPVPText.setX(360);
        winnerPVPText.setY(210);

        scorePVP1.setText("SCORE: " + score1);
        scorePVP1.setX(10);
        scorePVP1.setY(20);
        scorePVP2.setText("SCORE: " + score2);
        scorePVP2.setX(PVP_W - 70);
        scorePVP2.setY(20);
        scorePVP1.setEffect(new DropShadow());
        scorePVP2.setEffect(new DropShadow());

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(435);
        restartButton.setLayoutY(230);

        backToMainPVP.setEffect(dropShadow);
        backToMainPVP.setPrefHeight(32);
        backToMainPVP.setPrefWidth(32);

        backToMainPVP.setLayoutX(20);
        backToMainPVP.setLayoutY(30);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToMainPVP.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");
        pvpBall = new Ball(BALL_RADIUS,PVP_W / 2,PVP_H / 2);
        pvpBall.setFill(Color.PLUM);

        pvpRectangle1 = new Stick(1,100,20,PVP_H / 2,0);
        pvpRectangle2 = new Stick(2,100,20,PVP_H / 2,PVP_W - RECT_H);
        pvpRectangle1.setFill(Color.HOTPINK);
        pvpRectangle2.setFill(Color.CYAN);

        pvpBall.setEffect(dropShadow);
        pvpRectangle1.setEffect(dropShadow);
        pvpRectangle2.setEffect(dropShadow);

        restartButton.setEffect(dropShadow);
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVP.setDisable(true);
        backToMainPVP.setOpacity(0);

        pvpPane.setPrefHeight(PVP_H);
        pvpPane.setPrefWidth(PVP_W);
        pvpPane.setStyle("-fx-background-image: url('https://cdn.shopify.com/s/files/1/0575/0987/1774/files/1_6df15c2e-7475-4b2e-839e-3826bc5c02f6.png?v=1653967370')");
    }

    private static void moveRectanglesPVP() {
        pvpRectangle1.moveRectangle();
        pvpRectangle2.moveRectangle();
    }

    private static void collisionCheckPVP() throws IOException {
        switch (actionPVP) {
            case W:
                if (pvpRectangle1.getTranslateY() - pvpRectangle1.getSpeed() >= 0) {
                    pvpRectangle1.setTranslateY(pvpRectangle1.getTranslateY() - pvpRectangle1.getSpeed());
                }
                break;
            case S:
                if (pvpRectangle1.getTranslateY() + RECT_W + pvpRectangle1.getSpeed() <= PVP_H) {
                    pvpRectangle1.setTranslateY(pvpRectangle1.getTranslateY() + pvpRectangle1.getSpeed());
                }
                break;
            case UP:
                if (pvpRectangle2.getTranslateY() - pvpRectangle2.getSpeed() >= 0) {
                    pvpRectangle2.setTranslateY(pvpRectangle2.getTranslateY() - pvpRectangle2.getSpeed());
                }
                break;
            case DOWN:
                if (pvpRectangle2.getTranslateY() + RECT_W + pvpRectangle2.getSpeed() <= PVP_H) {
                    pvpRectangle2.setTranslateY(pvpRectangle2.getTranslateY() + pvpRectangle2.getSpeed());
                }
                break;
            case NONE:
                break;
        }

        //ball section

        //ball at left side
        if (pvpBall.getTranslateX() - BALL_RADIUS <= 0) {
            //stops the ball
            pvpBall.setXSpeed(0);
            pvpBall.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);
            pvpBall.setEffect(new BoxBlur(10,10,3));
            pvpRectangle1.setEffect(new BoxBlur(10,10,3));
            pvpRectangle2.setEffect(new BoxBlur(10,10,3));
            restartButton.setOpacity(1);
            backToMainPVP.setOpacity(1);
            backToMainPVP.setDisable(false);

            scorePVP1.setEffect(new BoxBlur(10,10,3));
            scorePVP2.setEffect(new BoxBlur(10,10,3));

            winnerPVPText.setOpacity(100);
            if (score1_PVP < score2_PVP) {
                winnerPVPText.setText("WINNER IS PLAYER 2!");
            }
            else if (score1_PVP > score2_PVP){
                winnerPVPText.setText("WINNER IS PLAYER 1!");
            }
            else if (score1_PVP == score2_PVP) {
                winnerPVPText.setX(400);
                winnerPVPText.setY(210);
                winnerPVPText.setText("NO WINNER :) !");
            }
        }

        //ball at right side
        else if (pvpBall.getTranslateX() + BALL_RADIUS >= PVP_W) {
            //stops the ball
            pvpBall.setXSpeed(0);
            pvpBall.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);
            pvpBall.setEffect(new BoxBlur(10,10,3));
            pvpRectangle1.setEffect(new BoxBlur(10,10,3));
            pvpRectangle2.setEffect(new BoxBlur(10,10,3));
            restartButton.setOpacity(1);
            backToMainPVP.setOpacity(1);
            backToMainPVP.setDisable(false);

            scorePVP1.setEffect(new BoxBlur(10,10,3));
            scorePVP2.setEffect(new BoxBlur(10,10,3));

            winnerPVPText.setOpacity(100);
            if (score1_PVP < score2_PVP) {
                winnerPVPText.setText("WINNER IS PLAYER 2!");
            }
            else if (score1_PVP > score2_PVP){
                winnerPVPText.setText("WINNER IS PLAYER 1!");
            }
            else if (score1_PVP == score2_PVP) {
                winnerPVPText.setX(400);
                winnerPVPText.setY(210);
                winnerPVPText.setText("NO WINNER :) !");
            }
        }

        //ball at bottom
        else if (pvpBall.getTranslateY() + BALL_RADIUS >= PVP_H) {
            pvpBall.setYSpeed(-pvpBall.yV);

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvpBall,(Color)pvpBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);
        }

        //ball at top
        else if (pvpBall.getTranslateY() - BALL_RADIUS <= 0) {
            pvpBall.setYSpeed(-pvpBall.yV);

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvpBall,(Color)pvpBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);
        }

        //ball meets the rectangle1
        else if (pvpBall.getTranslateX() - BALL_RADIUS <= RECT_H &&
                pvpBall.getTranslateY() + BALL_RADIUS >= pvpRectangle1.getTranslateY() &&
                pvpBall.getTranslateY() - BALL_RADIUS <= pvpRectangle1.getTranslateY() + RECT_W) {
            ++score1_PVP;
            pvpBall.setXSpeed(Math.abs(pvpBall.xV));

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvpBall,(Color)pvpBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);

            pvpBall.xV++;
            if (pvpBall.yV > 0) {
                ++pvpBall.yV;
            }
            else {
                --pvpBall.yV;
            }

            pvpBall.setXSpeed(pvpBall.xV);
            pvpBall.setYSpeed(pvpBall.yV);
        }

        //ball meets the rectangle2
        else if (pvpBall.getTranslateX() + BALL_RADIUS >= PVP_W - RECT_H &&
                pvpBall.getTranslateY() + BALL_RADIUS >= pvpRectangle2.getTranslateY() &&
                pvpBall.getTranslateY() - BALL_RADIUS <= pvpRectangle2.getTranslateY() + RECT_W) {
            ++score2_PVP;
            pvpBall.setXSpeed(Math.abs(pvpBall.xV));

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvpBall,(Color)pvpBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);

            pvpBall.xV++;
            if (pvpBall.yV > 0) {
                ++pvpBall.yV;
            }
            else {
                --pvpBall.yV;
            }

            pvpBall.setXSpeed(-pvpBall.xV);
            pvpBall.setYSpeed(pvpBall.yV);
        }
        scorePVP1.setText("SCORE: " + score1_PVP);
        scorePVP2.setText("SCORE: " + score2_PVP);
    }

    public static void runPVP() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(RUN_PVP.getRUN()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1) {
                pvpBall.moveBall();
                moveRectanglesPVP();
                //smooths out the animation
                Toolkit.getDefaultToolkit().sync();
                try {
                    collisionCheckPVP();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                delta--;
            }
        }
    }

    @FXML
    private static void restart_PVP() throws IOException {
        score1_PVP = 0;
        score2_PVP = 0;
        scorePVP1.setText("SCORE: " + score1_PVP);
        scorePVP2.setText("SCORE: " + score2_PVP);
        DropShadow dropShadow  = new DropShadow();
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);
        scorePVP1.setEffect(dropShadow);
        scorePVP2.setEffect(dropShadow);
        winnerPVPText.setOpacity(0);

        restartButton.setDisable(true);
        backToMainPVP.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVP.setOpacity(0);

        scorePVP1.setText("SCORE: " + score1);
        scorePVP1.setX(10);
        scorePVP1.setY(20);
        scorePVP2.setText("SCORE: " + score2);
        scorePVP2.setX(PVP_W - 70);
        scorePVP2.setY(20);

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(435);
        restartButton.setLayoutY(230);

        backToMainPVP.setEffect(dropShadow);
        backToMainPVP.setPrefHeight(32);
        backToMainPVP.setPrefWidth(32);

        backToMainPVP.setLayoutX(20);
        backToMainPVP.setLayoutY(30);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToMainPVP.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");

        pvpBall.setTranslateX(PVP_W / 2);
        pvpBall.setTranslateY(PVP_H / 2);
        pvpRectangle1.setTranslateX(0);
        pvpRectangle1.setTranslateY(PVP_H / 2);
        pvpRectangle2.setTranslateX(PVP_W - RECT_H);
        pvpRectangle2.setTranslateY(PVP_H / 2);

        pvpBall.setEffect(dropShadow);
        pvpRectangle1.setEffect(dropShadow);
        pvpRectangle2.setEffect(dropShadow);

        restartButton.setEffect(dropShadow);
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVP.setDisable(true);
        backToMainPVP.setOpacity(0);
        pvpPane.setEffect(dropShadow);

        pvpPane.setEffect(dropShadow);
        pvpBall.setXSpeed(2);
        pvpBall.setYSpeed(2);

        pvpStage.setOpacity(1);
    }

    private static void backToStartWhenButtonPressedPVP() throws IOException {
        RUN_PVP.setRUNFalse();
        pvpStage.close();
        playStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVP.setOpacity(0);
        backToMainPVP.setDisable(true);

        play();
    }
}
