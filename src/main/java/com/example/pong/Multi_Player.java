package com.example.pong;

import com.example.pong.interfaces.IName;
import com.example.pong.obejcts.Stop_Threads;
import com.example.pong.interfaces.IMode;
import com.example.pong.obejcts.Ball;
import com.example.pong.obejcts.Stick;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.pong.Single_Player.*;

public class Multi_Player implements IMode, IName {

    @FXML
    public static Button restartButton = new Button();
    public static UserAction action = UserAction.NONE;

    //for transition
    private static Random random1 = new Random();
    private static Random random2 = new Random();
    private static int randomColorGenerator1Ball;
    private static int randomColorGenerator2Ball;
    private static int randomColorGenerator1Rect;
    private static int randomColorGenerator2Rect;
    private static ArrayList<Color> colorsOfTheBall1 = new ArrayList<>();
    private static ArrayList<Color> colorsOfTheBall2 = new ArrayList<>();
    private static FillTransition transitionOfBall1;
    private static FillTransition transitionOfBall2;
    private static FillTransition transitionRect1;
    private static FillTransition transitionRect2;

    //sound
    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);

    //sizes
    private static final int APP_W = 500;
    private static final int APP_H = 700;
    private static final int MULTI_W = 800;
    private static final int MULTI_H = 700;

    public static Stop_Threads RUN_MULTI = new Stop_Threads();
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



    ///NAME OF PLAYERS

    private static Stage nameStage = new Stage();
    private static Scene nameScene;
    private static String name1, name2, color1, color2;

    @FXML
    private Button save = new Button();
    @FXML
    private Button start = new Button();
    //text fields
    @FXML
    private TextField color_1 = new TextField();
    @FXML
    private TextField color_2 = new TextField();
    @FXML
    private TextField name_player_1 = new TextField();
    @FXML
    private TextField name_player_2 = new TextField();

    //player names
    private static Text player_1_name_text = new Text(name1);
    private static Text player_2_name_text = new Text(name2);

    @FXML
    public void startMultiplayer() throws IOException {
        nameStage.close();
        playStage.close();
        multiplayerScene = new Scene(createMultiplayer());
        multiPlayerThread = new Thread(Multi_Player::runMultiplayer);
        multiPlayerThread.start();
        save_names();

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

    @Override
    public void start(Stage stage) throws IOException {
        stage.close();
        Parent window = FXMLLoader.load(getClass().getResource("name_input_multi.fxml"));
        nameScene = new Scene(window,WIDTH,HEIGHT);
        nameStage.setResizable(false);
        nameStage.setScene(nameScene);
        nameStage.setTitle("Name");
        nameStage.show();
    }

    @FXML
    public void save_names() {
        name1 = name_player_1.getText();
        name2 = name_player_2.getText();
        color1 = color_1.getText();
        color2 = color_2.getText();

        color_picker_for_name(color1,player_1_name_text);
        color_picker_for_name(color2,player_2_name_text);
        scoreMulti1.setFill(player_1_name_text.getFill());
        scoreMulti2.setFill(player_2_name_text.getFill());

        player_1_name_text.setText(name1);
        player_2_name_text.setText(name2);

        name_player_1.clear();
        name_player_2.clear();
        color_1.clear();
        color_2.clear();
    }

    private static void color_picker_for_name(String color,Text name) {
        if (color.equals("pink")) {
            name.setFill(Color.DEEPPINK);
        }
        else if (color.equals("blue")) {
            name.setFill(Color.CYAN);
        }
        else if (color.equals("yellow")) {
            name.setFill(Color.YELLOW);
        }
        else if (color.equals("lime") || color.equals("green")) {
            name.setFill(Color.CHARTREUSE);
        }
        else if (color.equals("grey")) {
            name.setFill(Color.GREY);
        }
    }

    @FXML
    public void clear() {
        name_player_1.clear();
        name_player_2.clear();
        color_1.clear();
        color_2.clear();
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

        multiplayerPane.getChildren().addAll(winnerText,scoreMulti1,scoreMulti2,player1Ball,player2Ball,player1Stick,player2Stick,separator,restartButton,backToStartButtonMultiPlayer,player_1_name_text,player_2_name_text);
        return multiplayerPane;
    }

    private static void initialize() {
        RUN_MULTI.setRUNTrue();

        DropShadow dropShadow  = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        Font font_name = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,20);
        Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);

        player_1_name_text.setX((MULTI_W / 2) / 2);
        player_1_name_text.setY(25);
        player_1_name_text.setFont(font_name);
        player_1_name_text.setEffect(dropShadow);
        player_2_name_text.setX((MULTI_W / 2) + (MULTI_W / 4));
        player_2_name_text.setY(25);
        player_2_name_text.setFont(font_name);
        player_2_name_text.setEffect(dropShadow);

        colorsOfTheBall1.add(Color.LIGHTBLUE);
        colorsOfTheBall1.add(Color.LIGHTCYAN);
        colorsOfTheBall1.add(Color.LIGHTPINK);
        colorsOfTheBall1.add(Color.LIGHTSALMON);
        colorsOfTheBall1.add(Color.LIGHTGREEN);
        colorsOfTheBall1.add(Color.LIGHTGRAY);
        colorsOfTheBall1.add(Color.CHARTREUSE);
        colorsOfTheBall1.add(Color.BLUEVIOLET);
        colorsOfTheBall1.add(Color.HOTPINK);
        colorsOfTheBall1.add(Color.CYAN);

        colorsOfTheBall2.add(Color.LIGHTBLUE);
        colorsOfTheBall2.add(Color.LIGHTCYAN);
        colorsOfTheBall2.add(Color.LIGHTPINK);
        colorsOfTheBall2.add(Color.LIGHTSALMON);
        colorsOfTheBall2.add(Color.LIGHTGREEN);
        colorsOfTheBall2.add(Color.LIGHTGRAY);
        colorsOfTheBall2.add(Color.CHARTREUSE);
        colorsOfTheBall2.add(Color.BLUEVIOLET);
        colorsOfTheBall2.add(Color.HOTPINK);
        colorsOfTheBall2.add(Color.CYAN);

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
        multiplayerPane.setBorder(new Border(new BorderStroke(Color.HOTPINK, BorderStrokeStyle.SOLID,null,new BorderWidths(3))));
        multiplayerPane.setStyle("-fx-background-color: '#232323'");
    }

    public static void runMultiplayer() {
        long lastTime = System.nanoTime();

        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(RUN_MULTI.getRUN() == true) {
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

            //color change
            randomColorGenerator1Ball = random1.nextInt(colorsOfTheBall1.size());
            transitionOfBall1 = new FillTransition(Duration.seconds(0.5),player1Ball,(Color)player1Ball.getFill(),colorsOfTheBall1.get(randomColorGenerator1Ball));
            transitionOfBall1.setAutoReverse(false);
            transitionOfBall1.setInterpolator(Interpolator.LINEAR);
            transitionOfBall1.play();

            color_Picker(multiplayerPane,colorsOfTheBall1, randomColorGenerator1Ball,3);
            playBallSound(ballSound,ballM);
        }
        //stops when balls hit the right side
        else if (player1Ball.getTranslateX() + BALL_RADIUS >= MULTI_W / 2) {
            player1Ball.setXSpeed(-player1Ball.xV);

            //color change
            randomColorGenerator1Ball = random1.nextInt(colorsOfTheBall1.size());
            transitionOfBall1 = new FillTransition(Duration.seconds(0.5),player1Ball,(Color)player1Ball.getFill(),colorsOfTheBall1.get(randomColorGenerator1Ball));
            transitionOfBall1.setAutoReverse(false);
            transitionOfBall1.setInterpolator(Interpolator.LINEAR);
            transitionOfBall1.play();

            color_Picker(multiplayerPane,colorsOfTheBall1, randomColorGenerator1Ball,3);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the top of the window
        else if (player1Ball.getTranslateY() - BALL_RADIUS <= 0){
            player1Ball.setYSpeed(-player1Ball.yV);

            //color change
            randomColorGenerator1Ball = random1.nextInt(colorsOfTheBall1.size());
            transitionOfBall1 = new FillTransition(Duration.seconds(0.5),player1Ball,(Color)player1Ball.getFill(),colorsOfTheBall1.get(randomColorGenerator1Ball));
            transitionOfBall1.setAutoReverse(false);
            transitionOfBall1.setInterpolator(Interpolator.LINEAR);
            transitionOfBall1.play();

            color_Picker(multiplayerPane,colorsOfTheBall1, randomColorGenerator1Ball,3);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the bottom of the window
        else if (player1Ball.getTranslateY() >= MULTI_H - BALL_RADIUS) {
            winnerText.setOpacity(100);
            if (score1 < score2) {
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS " + name2.toUpperCase() + "!");
            }
            else if (score1 > score2){
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS " + name1.toUpperCase() + "!");
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

            //color change
            randomColorGenerator1Ball = random1.nextInt(colorsOfTheBall1.size());
            randomColorGenerator1Rect = random1.nextInt(colorsOfTheBall1.size());
            transitionOfBall1 = new FillTransition(Duration.seconds(0.5),player1Ball,(Color)player1Ball.getFill(),colorsOfTheBall1.get(randomColorGenerator1Ball));
            transitionRect1 = new FillTransition(Duration.seconds(0.5),player1Stick,(Color)player1Stick.getFill(),colorsOfTheBall1.get(randomColorGenerator1Rect));

            transitionRect1.setAutoReverse(false);
            transitionRect1.setInterpolator(Interpolator.LINEAR);
            transitionOfBall1.setAutoReverse(false);
            transitionOfBall1.setInterpolator(Interpolator.LINEAR);
            transitionRect1.play();
            transitionOfBall1.play();

            color_Picker(multiplayerPane,colorsOfTheBall1, randomColorGenerator1Ball,3);
            transitionOfBall1.stop();
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

            //color change
            randomColorGenerator2Ball = random2.nextInt(colorsOfTheBall2.size());
            transitionOfBall2 = new FillTransition(Duration.seconds(0.5),player2Ball,(Color)player2Ball.getFill(),colorsOfTheBall2.get(randomColorGenerator2Ball));
            transitionOfBall2.setAutoReverse(false);
            transitionOfBall2.setInterpolator(Interpolator.LINEAR);
            transitionOfBall2.play();

            color_Picker(multiplayerPane,colorsOfTheBall2, randomColorGenerator2Ball,3);
            playBallSound(ballSound, ballM);
        }
        //stops when balls hit the right side
        else if (player2Ball.getTranslateX() + BALL_RADIUS >= MULTI_W) {
            player2Ball.setXSpeed(-player2Ball.xV);

            //color change
            randomColorGenerator2Ball = random2.nextInt(colorsOfTheBall2.size());
            transitionOfBall2 = new FillTransition(Duration.seconds(0.5),player2Ball,(Color)player2Ball.getFill(),colorsOfTheBall2.get(randomColorGenerator2Ball));
            transitionOfBall2.setAutoReverse(false);
            transitionOfBall2.setInterpolator(Interpolator.LINEAR);
            transitionOfBall2.play();

            color_Picker(multiplayerPane,colorsOfTheBall2, randomColorGenerator2Ball,3);
            playBallSound(ballSound, ballM);
        }
        //if ball meets the top of the window
        else if (player2Ball.getTranslateY() - BALL_RADIUS <= 0) {
            player2Ball.setYSpeed(-player2Ball.yV);

            //color change
            randomColorGenerator2Ball = random2.nextInt(colorsOfTheBall2.size());
            transitionOfBall2 = new FillTransition(Duration.seconds(0.5),player2Ball,(Color)player2Ball.getFill(),colorsOfTheBall2.get(randomColorGenerator2Ball));
            transitionOfBall2.setAutoReverse(false);
            transitionOfBall2.setInterpolator(Interpolator.LINEAR);
            transitionOfBall2.play();

            color_Picker(multiplayerPane,colorsOfTheBall2, randomColorGenerator2Ball,3);
            playBallSound(ballSound, ballM);
        }
        //if ball meets the bottom of the window
        else if (player2Ball.getTranslateY() >= MULTI_H - BALL_RADIUS) {
            winnerText.setOpacity(100);
            if (score1 < score2) {
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS " + name2.toUpperCase() + "!");
            }
            else if (score1 > score2){
                winnerText.setX(APP_W / 2 + 25);
                winnerText.setY(APP_H / 2 - 90);
                winnerText.setText("WINNER IS " + name1.toUpperCase() + "!");
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

            //color change
            randomColorGenerator2Ball = random2.nextInt(colorsOfTheBall2.size());
            randomColorGenerator2Rect = random2.nextInt(colorsOfTheBall2.size());
            transitionOfBall2 = new FillTransition(Duration.seconds(0.5),player2Ball,(Color)player2Ball.getFill(),colorsOfTheBall2.get(randomColorGenerator2Ball));
            transitionRect2 = new FillTransition(Duration.seconds(0.5),player2Stick,(Color) player2Stick.getFill(),colorsOfTheBall2.get(randomColorGenerator2Rect));

            transitionRect2.setInterpolator(Interpolator.LINEAR);
            transitionRect2.setAutoReverse(false);
            transitionOfBall2.setAutoReverse(false);
            transitionOfBall2.setInterpolator(Interpolator.LINEAR);
            transitionRect2.play();
            transitionOfBall2.play();

            color_Picker(multiplayerPane,colorsOfTheBall2, randomColorGenerator2Ball,3);
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
        RUN_MULTI.setRUNTrue();
        restartButton.setOpacity(0);
        restartButton.setDisable(true);

        DropShadow dropShadow  = new DropShadow();
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
        RUN_MULTI.setRUNFalse();
        multiplayerStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonMultiPlayer.setOpacity(0);
        backToStartButtonMultiPlayer.setDisable(true);

        Controller.play();
    }
}
