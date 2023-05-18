package com.example.pong;

import com.example.pong.interfaces.IName;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import static com.example.pong.Single_Player.*;
import static com.example.pong.Multi_Player.*;
import static com.example.pong.Player_Vs_Player.*;
import static com.example.pong.Player_Vs_Computer.*;

public class Controller {

    ///START THE GAME
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
    public void single_player() throws IOException {
        startTheGame();
    }

    ///MULTIPLAYER
    @FXML
    private void multi_player() throws IOException {
        IName name = new Multi_Player();
        name.start(playStage);
        //startMultiplayer();
    }

    ///PLAYER_VS_PLAYER
    @FXML
    private void player_vs_player() throws IOException {
        startPVP();
    }

    ///PLAYER_VS_COMPUTER
    @FXML
    private void player_vs_computer() throws IOException {
        start_PVC();
    }
}