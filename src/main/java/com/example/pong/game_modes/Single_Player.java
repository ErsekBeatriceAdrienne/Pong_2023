package com.example.pong.game_modes;

import com.example.pong.Controller;
import com.example.pong.Pong_Game;
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
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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

public class Single_Player extends Controller implements IMode {

    public static UserAction action = UserAction.NONE;
    @FXML
    private static Ball ball;
    @FXML
    private static Stick rectangle;

    //ball color transition
    private static Random random = new Random();
    private static int randomColorGeneratorBall;
    private static int randomColorGeneratorRectangle;
    private static ArrayList <Color> colorsOfTheBall = new ArrayList<>();
    private static FillTransition transitionOfBall;

    //rectangle color transition
    private static FillTransition transitionRectangle;

    @FXML
    public static Button restartButton = new Button();

    private static String ballFile = "ball_sound.mp3";
    public static Media ballM = new Media(new File(ballFile).toURI().toString());
    public static MediaPlayer ballSound = new MediaPlayer(ballM);

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

    public static void startTheGame() throws IOException {
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
        singlePlayerStage.getIcons().add(new Image(Single_Player.class.getResourceAsStream("/com/example/pong/style/game_icon.jpg")));
        singlePlayerStage.show();
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

        root.getChildren().addAll(scoreText, ball, rectangle, restartButton,backToStartButtonSinglePlayer,finalScoreText);
        return root;
    }

    private static void helper() {
        RUN.setRUNTrue();

        colorsOfTheBall.add(Color.SKYBLUE);
        colorsOfTheBall.add(Color.DEEPPINK);
        colorsOfTheBall.add(Color.GREENYELLOW);
        colorsOfTheBall.add(Color.YELLOW);
        colorsOfTheBall.add(Color.PURPLE);
        colorsOfTheBall.add(Color.ORANGE);
        colorsOfTheBall.add(Color.BLUEVIOLET);
        colorsOfTheBall.add(Color.HOTPINK);
        colorsOfTheBall.add(Color.CYAN);

        singlePlayerStage.setTitle("Singleplayer Ball Game");
        DropShadow shadow = new DropShadow();
        scoreText.setEffect(shadow);
        finalScoreText.setOpacity(0);
        finalScoreText.setFill(Color.DEEPPINK);
        finalScoreText.setStroke(Color.BLACK);
        finalScoreText.setStrokeWidth(0.5);
        finalScoreText.setX(162);
        finalScoreText.setY(250);
        DropShadow dropShadow  = new DropShadow();
        dropShadow.setOffsetX(4.0f);
        dropShadow.setOffsetY(4.0f);
        dropShadow.setColor(Color.BLACK);

        scoreText.setText("SCORE: " + score);
        scoreText.setFill(Color.HOTPINK);
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
        //root.setStyle("-fx-background-image: url('https://mcdn.wallpapersafari.com/medium/99/61/CI1pFG.png')");
        root.setStyle("-fx-background-color: '#232323'");
        root.setBorder(new Border(new BorderStroke(Color.HOTPINK, BorderStrokeStyle.SOLID,null,new BorderWidths(3))));

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

            //color change
            randomColorGeneratorBall = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),ball,(Color)ball.getFill(),colorsOfTheBall.get(randomColorGeneratorBall));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            color_Picker(randomColorGeneratorBall,3);
            playBallSound(ballSound,ballM);
        }

        //if ball hits the right side of the window
        else if (ball.getTranslateX() + BALL_RADIUS >= APP_W){
            ball.setXSpeed(-ball.xV);

            //color change
            randomColorGeneratorBall = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),ball,(Color) ball.getFill(),colorsOfTheBall.get(randomColorGeneratorBall));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            color_Picker(randomColorGeneratorBall,3);
            playBallSound(ballSound,ballM);
        }

        //if ball meets the bottom of the window
        else if (ball.getTranslateY() >= APP_H - BALL_RADIUS) {
            ball.setXSpeed(0);
            ball.setYSpeed(0);
            restartButton.setDisable(false);
            DropShadow dropShadow  = new DropShadow();
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
            //root.setBorder(new Border(new BorderStroke(ball.getFill(), BorderStrokeStyle.SOLID,null,new BorderWidths(3))));
            color_Picker(randomColorGeneratorBall,3);

            finalScoreText.setOpacity(1);
            Font font = Font.font("new times roman", FontWeight.BOLD, FontPosture.REGULAR,30);
            finalScoreText.setText(" Your score : " + score);
            finalScoreText.setFont(font);
            finalScoreText.setEffect(dropShadow);
            transitionOfBall.stop();
            transitionRectangle.stop();
        }

        //if ball meets the top of the window
        else if (ball.getTranslateY() - BALL_RADIUS <= 0){
            ball.setYSpeed(-ball.yV);

            //color change
            randomColorGeneratorBall = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),ball,(Color) ball.getFill(),colorsOfTheBall.get(randomColorGeneratorBall));
            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionOfBall.play();

            color_Picker(randomColorGeneratorBall,3);
            playBallSound(ballSound,ballM);
        }
        //if ball meets the rectangle
        else if (ball.getTranslateY() + BALL_RADIUS >= APP_H - RECT_H
                && ball.getTranslateX() + BALL_RADIUS >= rectangle.getTranslateX()
                && ball.getTranslateX() - BALL_RADIUS <= rectangle.getTranslateX() + RECT_W) {
            ++score;

            ball.setYSpeed(Math.abs(ball.yV));

            //color change
            randomColorGeneratorBall = random.nextInt(colorsOfTheBall.size());
            randomColorGeneratorRectangle = random.nextInt(colorsOfTheBall.size());
            transitionOfBall = new FillTransition(Duration.seconds(0.5),ball,(Color) ball.getFill(),colorsOfTheBall.get(randomColorGeneratorBall));
            transitionRectangle = new FillTransition(Duration.seconds(0.5),rectangle,(Color)rectangle.getFill(),colorsOfTheBall.get(randomColorGeneratorRectangle));

            transitionOfBall.setAutoReverse(false);
            transitionOfBall.setInterpolator(Interpolator.LINEAR);
            transitionRectangle.setInterpolator(Interpolator.LINEAR);
            transitionRectangle.setAutoReverse(false);
            transitionRectangle.play();
            transitionOfBall.play();

            color_Picker(randomColorGeneratorBall,3);
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

        rectangle.setFill(Color.HOTPINK);
        ball.setFill(Color.CYAN);
        root.setBorder(new Border(new BorderStroke(Color.HOTPINK, BorderStrokeStyle.SOLID,null,BorderStroke.THICK)));

        //set effect
        DropShadow dropShadow  = new DropShadow();
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

    private static void color_Picker(int random_number, int width) {
        root.setBorder(new Border(new BorderStroke(colorsOfTheBall.get(random_number), BorderStrokeStyle.SOLID,null,new BorderWidths(width))));
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
