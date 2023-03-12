package com.example.pong;

import javafx.scene.shape.Rectangle;

public class Stick extends Rectangle {

    private static final int APP_W = 500;
    private static final int APP_H = 700;

    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    private int rectagnleId;
    public int xV;
    private int speed = 5;

    Stick(int rectagnleId) {
        super(RECT_W,RECT_H);

        this.setTranslateX(APP_W / 2.5);
        this.setTranslateY(APP_H - RECT_H);

        this.rectagnleId = rectagnleId;
    }

    public void setYDirection(int xDirection) {
        xV = xDirection;
    }

    public void moveRectangle() {
        this.setTranslateX(this.getTranslateX() + xV);
    }

    public int getSpeed() {
        return speed;
    }

    public int getRectagnleId() {
        return rectagnleId;
    }
}
