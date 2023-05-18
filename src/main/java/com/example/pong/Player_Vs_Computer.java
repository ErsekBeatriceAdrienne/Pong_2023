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
import static com.example.pong.Single_Player.playBallSound;
import static com.example.pong.Single_Player.playStage;

public class Player_Vs_Computer implements IMode {

    private static Random random = new Random();

    private static int randomColorGenerator;
    private static ArrayList<Color> colorsOfTheBall = new ArrayList<>();
    private static FillTransition transitionOfBall;

    @FXML
    public static Button restartButton = new Button();
    enum UserActionPVC {
        NONE, UP, DOWN, W, S
    }
    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);
    public static UserActionPVC actionPVC = UserActionPVC.NONE;
    @FXML
    private static Button pvcButton = new Button();

    public static Stop_Threads RUN_PVC = new Stop_Threads();
    public static Scene pvcScene;
    public static Stage pvcStage = new Stage();
    @FXML
    public static Button backToMainPVC = new Button();
    public static final int BALL_RADIUS = 10;
    public static final int RECT_W = 100;
    public static final int RECT_H = 20;
    public static final int PVC_W = 1000;
    public static final int PVC_H = 600;

    public static int score1_PVC = 0;
    public static int score2_PVC = 0;
    public static Text scorePVC1 = new Text();
    public static Text scorePVC2 = new Text();
    public static Text winnerPVCText = new Text();
    @FXML
    public static Pane pvcPane = new Pane();
    public static Thread pvcThread;

    //objects
    public static Ball pvcBall;
    public static Stick pvcRectangle1;
    public static Stick pvcRectangle2;

    public static void start_PVC() throws IOException {
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
        pvcStage.getIcons().add(new Image(Player_Vs_Player.class.getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        pvcStage.show();
    }

    public static Parent createContent_PVC() throws IOException {
        pvcInitializer();

        backToMainPVC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    backToStartWhenButtonPressedPVC();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    restart_PVC();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pvcPane.getChildren().addAll(scorePVC1,scorePVC2,pvcBall,pvcRectangle1,pvcRectangle2,backToMainPVC,restartButton,winnerPVCText);
        return pvcPane;
    }

    public static void pvcInitializer() {

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

        score1_PVC = 0;
        score2_PVC = 0;
        RUN_PVC.setRUNTrue();

        DropShadow dropShadow  = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);
        winnerPVCText.setOpacity(0);
        winnerPVCText.setFill(Color.HOTPINK);
        winnerPVCText.setStroke(Color.BLACK);
        winnerPVCText.setStrokeWidth(0.5);
        winnerPVCText.setFont(font);
        winnerPVCText.setEffect(dropShadow);
        winnerPVCText.setX(360);
        winnerPVCText.setY(210);

        scorePVC1.setText("SCORE: " + score1_PVC);
        scorePVC1.setX(10);
        scorePVC1.setY(20);
        scorePVC2.setText("SCORE: " + score2_PVC);
        scorePVC2.setX(PVC_W - 70);
        scorePVC2.setY(20);
        scorePVC1.setEffect(dropShadow);
        scorePVC2.setEffect(dropShadow);

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(435);
        restartButton.setLayoutY(230);

        backToMainPVC.setEffect(dropShadow);
        backToMainPVC.setPrefHeight(32);
        backToMainPVC.setPrefWidth(32);

        backToMainPVC.setLayoutX(20);
        backToMainPVC.setLayoutY(30);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToMainPVC.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");
        pvcBall = new Ball(BALL_RADIUS,PVC_W / 2,PVC_H / 2);
        pvcBall.setFill(Color.PLUM);

        pvcRectangle1 = new Stick(1,100,20,PVC_H / 2,0);
        pvcRectangle2 = new Stick(2,100,20,PVC_H / 2,PVC_W - RECT_H);
        pvcRectangle1.setFill(Color.HOTPINK);
        pvcRectangle2.setFill(Color.CYAN);

        pvcBall.setEffect(dropShadow);
        pvcRectangle1.setEffect(dropShadow);
        pvcRectangle2.setEffect(dropShadow);

        restartButton.setEffect(dropShadow);
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVC.setDisable(true);
        backToMainPVC.setOpacity(0);

        pvcPane.setPrefHeight(PVC_H);
        pvcPane.setPrefWidth(PVC_W);
        pvcPane.setStyle("-fx-background-image: url('https://cdn.shopify.com/s/files/1/0575/0987/1774/files/1_6df15c2e-7475-4b2e-839e-3826bc5c02f6.png?v=1653967370')");
    }

    public static void restart_PVC() throws IOException {
        score1_PVC = 0;
        score2_PVC = 0;

        scorePVC1.setText("SCORE: " + score1_PVC);
        scorePVC2.setText("SCORE: " + score2_PVC);
        DropShadow dropShadow  = new DropShadow();
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);
        scorePVC1.setEffect(dropShadow);
        scorePVC2.setEffect(dropShadow);
        winnerPVCText.setOpacity(0);

        restartButton.setDisable(true);
        backToMainPVC.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVC.setOpacity(0);

        scorePVC1.setText("SCORE: " + score1_PVC);
        scorePVC1.setX(10);
        scorePVC1.setY(20);
        scorePVC2.setText("SCORE: " + score2_PVC);
        scorePVC2.setX(PVC_W - 70);
        scorePVC2.setY(20);

        restartButton.setPrefHeight(117);
        restartButton.setPrefWidth(113);
        restartButton.setText("");

        restartButton.setLayoutX(435);
        restartButton.setLayoutY(230);

        backToMainPVC.setEffect(dropShadow);
        backToMainPVC.setPrefHeight(32);
        backToMainPVC.setPrefWidth(32);

        backToMainPVC.setLayoutX(20);
        backToMainPVC.setLayoutY(30);

        restartButton.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 5; -fx-background-color: transparent;-fx-shape: \"M58.828,16.208l-3.686,4.735c7.944,6.182,11.908,16.191,10.345,26.123C63.121,62.112,48.954,72.432,33.908,70.06C18.863,67.69,8.547,53.522,10.912,38.477c1.146-7.289,5.063-13.694,11.028-18.037c5.207-3.79,11.433-5.613,17.776-5.252l-5.187,5.442l3.848,3.671l8.188-8.596l0.002,0.003l3.668-3.852L46.39,8.188l-0.002,0.001L37.795,0l-3.671,3.852l5.6,5.334c-7.613-0.36-15.065,1.853-21.316,6.403c-7.26,5.286-12.027,13.083-13.423,21.956c-2.879,18.313,9.676,35.558,27.989,38.442c1.763,0.277,3.514,0.411,5.245,0.411c16.254-0.001,30.591-11.85,33.195-28.4C73.317,35.911,68.494,23.73,58.828,16.208z\"");
        backToMainPVC.setStyle("-fx-border-color: '#ff1ea9'; -fx-stroke-width: 4; -fx-stroke: '#ff3535'; -fx-border-width: 4; -fx-background-color: transparent; -fx-shape: \"M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z\"; -fx-stroke: '#000'; -fx-stroke-line-cap: round; -fx-stroke-line-join: round; -fx-stroke-width: 48mp");

        pvcBall.setTranslateX(PVC_W / 2);
        pvcBall.setTranslateY(PVC_H / 2);
        pvcRectangle1.setTranslateX(0);
        pvcRectangle1.setTranslateY(PVC_H / 2);
        pvcRectangle2.setTranslateX(PVC_W - RECT_H);
        pvcRectangle2.setTranslateY(PVC_H / 2);

        pvcBall.setEffect(dropShadow);
        pvcRectangle1.setEffect(dropShadow);
        pvcRectangle2.setEffect(dropShadow);

        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVC.setDisable(true);
        backToMainPVC.setOpacity(0);
        pvcPane.setEffect(dropShadow);

        pvcPane.setEffect(dropShadow);
        pvcBall.setXSpeed(2);
        pvcBall.setYSpeed(2);

        pvcStage.setOpacity(1);
    }

    public static void moveRectanglesPVC() {
        pvcRectangle1.moveRectangle();
    }

    public static void collisionCheckPVC() throws IOException {
        switch (actionPVC) {
            case W:
            case UP:
                if (pvcRectangle1.getTranslateY() - pvcRectangle1.getSpeed() >= 0) {
                    pvcRectangle1.setTranslateY(pvcRectangle1.getTranslateY() - pvcRectangle1.getSpeed());
                }
                break;
            case S:
            case DOWN:
                if (pvcRectangle1.getTranslateY() + RECT_W + pvcRectangle1.getSpeed() <= PVC_H) {
                    pvcRectangle1.setTranslateY(pvcRectangle1.getTranslateY() + pvcRectangle1.getSpeed());
                }
                break;
            case NONE:
                break;
        }

        //ball at left side
        if (pvcBall.getTranslateX() - BALL_RADIUS <= 0) {
            //stops the ball
            pvcBall.setXSpeed(0);
            pvcBall.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);
            pvcBall.setEffect(new BoxBlur(10,10,3));
            pvcRectangle1.setEffect(new BoxBlur(10,10,3));
            pvcRectangle2.setEffect(new BoxBlur(10,10,3));
            restartButton.setOpacity(1);
            backToMainPVC.setOpacity(1);
            backToMainPVC.setDisable(false);

            scorePVC1.setEffect(new BoxBlur(10,10,3));
            scorePVC2.setEffect(new BoxBlur(10,10,3));

            winnerPVCText.setOpacity(100);
            if (score1_PVC < score2_PVC) {
                winnerPVCText.setX(430);
                winnerPVCText.setY(210);
                winnerPVCText.setText("YOU LOST!");
            }
            else if (score1_PVC > score2_PVC){
                winnerPVCText.setX(430);
                winnerPVCText.setY(210);
                winnerPVCText.setText("YOU WON!");
            }
            else if (score1_PVC == score2_PVC) {
                winnerPVCText.setX(400);
                winnerPVCText.setY(210);
                winnerPVCText.setText("NO WINNER :) !");
            }
        }

        //ball at right side
        else if (pvcBall.getTranslateX() + BALL_RADIUS >= PVC_W) {
            //stops the ball
            pvcBall.setXSpeed(0);
            pvcBall.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
            dropShadow.setOffsetX(4.0f);
            dropShadow.setOffsetY(4.0f);
            dropShadow.setColor(Color.BLACK);
            restartButton.setEffect(dropShadow);
            pvcBall.setEffect(new BoxBlur(10,10,3));
            pvcRectangle1.setEffect(new BoxBlur(10,10,3));
            pvcRectangle2.setEffect(new BoxBlur(10,10,3));
            restartButton.setOpacity(1);
            backToMainPVC.setOpacity(1);
            backToMainPVC.setDisable(false);

            scorePVC1.setEffect(new BoxBlur(10,10,3));
            scorePVC2.setEffect(new BoxBlur(10,10,3));

            winnerPVCText.setOpacity(100);
            if (score1_PVC < score2_PVC) {
                winnerPVCText.setX(430);
                winnerPVCText.setY(210);
                winnerPVCText.setText("YOU LOST!");
            }
            else if (score1_PVC > score2_PVC){
                winnerPVCText.setX(430);
                winnerPVCText.setY(210);
                winnerPVCText.setText("YOU WON!");
            }
            else if (score1_PVC == score2_PVC) {
                winnerPVCText.setX(400);
                winnerPVCText.setY(210);
                winnerPVCText.setText("NO WINNER :)!");
            }
        }

        //ball at bottom
        else if (pvcBall.getTranslateY() + BALL_RADIUS >= PVC_H) {
            pvcBall.setYSpeed(-pvcBall.yV);

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvcBall,(Color)pvcBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);
        }

        //ball at top
        else if (pvcBall.getTranslateY() - BALL_RADIUS <= 0) {
            pvcBall.setYSpeed(-pvcBall.yV);

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvcBall,(Color)pvcBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);
        }

        //ball meets the rectangle1
        else if (pvcBall.getTranslateX() - BALL_RADIUS <= RECT_H &&
                pvcBall.getTranslateY() + BALL_RADIUS >= pvcRectangle1.getTranslateY() &&
                pvcBall.getTranslateY() - BALL_RADIUS <= pvcRectangle1.getTranslateY() + RECT_W) {
            ++score1_PVC;
            pvcBall.setXSpeed(Math.abs(pvcBall.xV));

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvcBall,(Color)pvcBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);

            pvcBall.xV++;
            if (pvcBall.yV > 0) {
                ++pvcBall.yV;
            }
            else {
                --pvcBall.yV;
            }

            pvcBall.setXSpeed(pvcBall.xV);
            pvcBall.setYSpeed(pvcBall.yV);
        }

        //ball meets the rectangle2
        else if (pvcBall.getTranslateX() + BALL_RADIUS >= PVC_W - RECT_H &&
                pvcBall.getTranslateY() + BALL_RADIUS >= pvcRectangle2.getTranslateY() &&
                pvcBall.getTranslateY() - BALL_RADIUS <= pvcRectangle2.getTranslateY() + RECT_W) {
            ++score2_PVC;
            pvcBall.setXSpeed(Math.abs(pvcBall.xV));

            //color change
            randomColorGenerator = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),pvcBall,(Color)pvcBall.getFill(),colorsOfTheBall.get(randomColorGenerator));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            playBallSound(ballSound,ballM);

            pvcBall.xV++;
            if (pvcBall.yV > 0) {
                ++pvcBall.yV;
            }
            else {
                --pvcBall.yV;
            }

            pvcBall.setXSpeed(-pvcBall.xV);
            pvcBall.setYSpeed(pvcBall.yV);
        }
        pvcRectangle2.moveWithBall(pvcBall);
        scorePVC1.setText("SCORE: " + score1_PVC);
        scorePVC2.setText("SCORE: " + score2_PVC);
    }

    public static void run_PVC() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (RUN_PVC.getRUN()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                pvcBall.moveBall();
                moveRectanglesPVC();
                pvcBall.moveBallWithRectangle(pvcRectangle2);
                Toolkit.getDefaultToolkit().sync();
                try {
                    collisionCheckPVC();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                delta--;
            }
        }
    }

    public static void backToStartWhenButtonPressedPVC() throws IOException {
        RUN_PVC.setRUNFalse();
        pvcStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToMainPVC.setOpacity(0);
        backToMainPVC.setDisable(true);

        play();
    }
}
