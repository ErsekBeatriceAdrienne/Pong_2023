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
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Controller {

    enum UserAction {
        NONE, LEFT, RIGHT, A, D
    }

    private static StopThreads RUN = new StopThreads();
    private static StopThreads RUN2 = new StopThreads();

    private static int score = 0;
    private static int score1 = 0;
    private static int score2 = 0;
    private static Text scoreText = new Text();
    private static Text scoreMulti1 = new Text();
    private static Text scoreMulti2 = new Text();
    private static Text winnerText = new Text();

    private static Scene scene;
    private static Scene playScene;
    private static Scene sceneGameOver;
    private static Scene multiplayerScene;

    private static DropShadow dropShadow;

    @FXML
    private static Pane root = new Pane();
    @FXML
    private static Pane multiplayerPane = new Pane();

    private static Stage playStage = new Stage();
    @FXML
    private static Stage singlePlayerStage = new Stage();
    private static Stage stageGameOver = new Stage();
    private static Stage restartStage = new Stage();
    private static Stage multiplayerStage = new Stage();

    private static Thread singlePlayerThread;
    private static Thread multiPlayerThread;

    @FXML
    Button singlePlayerButton = new Button();
    @FXML
    Button playButton = new Button();
    @FXML
    private static Button restartButton = new Button();
    @FXML
    private static Button backToStartButtonSinglePlayer = new Button();
    @FXML
    private static Button backToStartButtonMultiPlayer = new Button();

    private static final int APP_W = 500;
    private static final int APP_H = 700;

    private static final int MULTI_W = 800;
    private static final int MULTI_H = 700;

    private static final int BALL_RADIUS = 10;
    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    @FXML
    private static Line separator = new Line();

    private static String ballFile = "ball_sound.mp3";
    private static Media ballM = new Media(new File(ballFile).toURI().toString());
    private static MediaPlayer ballSound = new MediaPlayer(ballM);

    private static String rectangleFile = "rectangle.mp3";
    private static Media rectangleM = new Media(new File(rectangleFile).toURI().toString());
    private static MediaPlayer rectangleSound = new MediaPlayer(rectangleM);

    @FXML
    private static Ball ball;
    private static Ball player1Ball;
    private static Ball player2Ball;
    @FXML
    private static Stick rectangle;
    private static Stick player1Stick;
    private static Stick player2Stick;

    private static UserAction action = UserAction.NONE;


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

        root.getChildren().addAll(scoreText, ball, rectangle, restartButton,backToStartButtonSinglePlayer);
        return root;
    }

    private static void helper() {
        RUN.setRUNTrue();

        dropShadow = new DropShadow();
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
            backToStartButtonSinglePlayer.setOpacity(1);
            backToStartButtonSinglePlayer.setDisable(false);
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
            ++score;

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

        scoreText.setText("SCORE : " + score);
    }

    @FXML
    public void startTheGame() throws IOException {
        playStage.close();
        RUN.setRUNTrue();
        scene = new Scene(createContent());
        singlePlayerThread = new Thread(Controller::run);
        singlePlayerThread.start();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                case A:
                    action = Controller.UserAction.LEFT;
                    break;
                case RIGHT:
                case D:
                    action = Controller.UserAction.RIGHT;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                case A:
                case RIGHT:
                case D:
                    action = Controller.UserAction.NONE;
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

    @FXML
    private static void restartGame() throws IOException {
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
        ball.setEffect(dropShadow);
        rectangle.setEffect(dropShadow);

        //setting the initial speed for ball
        ball.setX(2);
        ball.setY(2);

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


    //opens a different window for game over
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

    private static void playBallSound(MediaPlayer music, Media media) {
        music.play();
        music.setStopTime(media.getDuration());
        music.seek(new Duration(0));
    }


    ///MULTIPLAYER

    private Parent createMultiplayer() throws IOException {
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
                   restartButton.setOpacity(0);
                   restartButton.setDisable(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        multiplayerPane.getChildren().addAll(winnerText,scoreMulti1,scoreMulti2,player1Ball,player2Ball,player1Stick,player2Stick,separator,restartButton,backToStartButtonMultiPlayer);
        return multiplayerPane;
    }

    @FXML
    private void startMultiplayer() throws IOException {
        playStage.close();
        RUN2.setRUNTrue();
        multiplayerScene = new Scene(createMultiplayer());
        multiPlayerThread = new Thread(Controller::runMultiplayer);
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

    private static void initialize() {
        dropShadow = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        Font font = Font.font("new times roman",FontWeight.BOLD,FontPosture.REGULAR,30);
        winnerText.setOpacity(0);
        winnerText.setFill(Color.HOTPINK);
        winnerText.setStroke(Color.BLACK);
        winnerText.setStrokeWidth(0.5);
        winnerText.setFont(font);
        winnerText.setEffect(dropShadow);
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

    private static void runMultiplayer() {
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
            player1Ball.setX(0);
            player1Ball.setY(0);
            player2Ball.setX(0);
            player2Ball.setY(0);

            restartButton.setDisable(false);
            backToStartButtonMultiPlayer.setDisable(false);

            player1Ball.setEffect(new BoxBlur(10,10,3));
            player1Stick.setEffect(new BoxBlur(10,10,3));
            player2Ball.setEffect(new BoxBlur(10,10,3));
            player2Stick.setEffect(new BoxBlur(10,10,3));
            //multiplayerPane.setEffect(new GaussianBlur());

            restartButton.setEffect(dropShadow);

            restartButton.setOpacity(1);
            backToStartButtonMultiPlayer.setOpacity(1);
        }
    }

    private static void collisionBall1() {
        //stops when balls hit the left side
        if (player1Ball.getTranslateX() - BALL_RADIUS <= 0) {
            player1Ball.setX(Math.abs(player1Ball.xV));
            playBallSound(ballSound,ballM);
        }
        //stops when balls hit the right side
        else if (player1Ball.getTranslateX() + BALL_RADIUS >= MULTI_W / 2) {
            player1Ball.setX(-player1Ball.xV);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the top of the window
        else if (player1Ball.getTranslateY() - BALL_RADIUS <= 0){
            player1Ball.setY(-player1Ball.yV);
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
            player1Ball.setX(0);
            player1Ball.setY(0);
            restartButton.setDisable(false);
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
            player1Ball.setY(Math.abs(player1Ball.yV));
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

            player1Ball.setX(player1Ball.xV);
            player1Ball.setY(-player1Ball.yV);
        }
        scoreMulti1.setText("SCORE : " + score1);
    }

    private static void collisionBall2() {
        //stops when balls hit the left side
        if (player2Ball.getTranslateX() - BALL_RADIUS <= MULTI_W / 2) {
            player2Ball.setX(Math.abs(player2Ball.xV));
            playBallSound(ballSound, ballM);
        }
        //stops when balls hit the right side
        else if (player2Ball.getTranslateX() + BALL_RADIUS >= MULTI_W) {
            player2Ball.setX(-player2Ball.xV);
            playBallSound(ballSound, ballM);
        }
        //if ball meets the top of the window
        else if (player2Ball.getTranslateY() - BALL_RADIUS <= 0) {
            player2Ball.setY(-player2Ball.yV);
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

            player2Ball.setX(0);
            player2Ball.setY(0);
            restartButton.setDisable(false);
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
            player2Ball.setY(Math.abs(player2Ball.yV));
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

            player2Ball.setX(player2Ball.xV);
            player2Ball.setY(-player2Ball.yV);
        }
        scoreMulti2.setText("SCORE : " + score2);
    }

    private static void restartMultiplayer() throws IOException {
        winnerText.setOpacity(0);
        score1 = 0;
        score2 = 0;
        RUN2.setRUNTrue();

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
        player1Ball.setX(2);
        player1Ball.setY(2);
        player2Ball.setX(2);
        player2Ball.setY(2);

        multiplayerStage.setOpacity(1);
    }


    ///BACK TO THE MAIN PAGE

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

    private static void backToStartWhenButtonPressedMultiPlayer() throws IOException {
        RUN2.setRUNFalse();
        multiplayerStage.close();
        restartButton.setDisable(true);
        restartButton.setOpacity(0);
        backToStartButtonMultiPlayer.setOpacity(0);
        backToStartButtonMultiPlayer.setDisable(true);

        play();
    }
}


