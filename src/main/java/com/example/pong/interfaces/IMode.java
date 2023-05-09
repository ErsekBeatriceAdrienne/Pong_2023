package com.example.pong.interfaces;

import javafx.scene.Parent;

public interface IMode {

    //statics
    static Parent createGame() {
        return null;
    }
    static void initialize() {
    }
    static void run() {

    }
    static void moveRectangles() {}
    static void collisionCheck() {}
    static void restart() {}
    static void backToStartWhenButtonPressed() {}
}
