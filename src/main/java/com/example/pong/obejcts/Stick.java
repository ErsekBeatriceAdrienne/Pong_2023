package com.example.pong.obejcts;

import javafx.scene.shape.Rectangle;

import java.awt.event.KeyEvent;

public class Stick extends Rectangle {

    private static final int RECT_W = 100;
    private static final int RECT_H = 20;

    private int rectagnleId;
    public int xV;
    private int speed = 5;

    public Stick(int rectagnleId, double APP_W, double APP_H) {
        super(RECT_W,RECT_H);

        this.setTranslateX(APP_W);
        this.setTranslateY(APP_H);

        this.rectagnleId = rectagnleId;
    }

    public Stick(int ID, int RECT_H, int RECT_W, double APP_H, double APP_W) {
        super(RECT_W,RECT_H);

        this.setTranslateX(APP_W);
        this.setTranslateY(APP_H);

        this.rectagnleId = ID;
    }

    public void keyPressed(KeyEvent e) {
        switch(this.rectagnleId) {
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    setYDirection(-speed);
                }
                if(e.getKeyCode() == KeyEvent.VK_D) {
                    setYDirection(speed);
                }
                break;
            case 2:
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    setYDirection(-speed);
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    setYDirection(speed);
                }
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch(this.rectagnleId) {
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_A) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_D) {
                    setYDirection(0);
                }
                break;
            case 2:
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    setYDirection(0);
                }
                break;
        }
    }


    public void setYDirection(int xDirection) {
        xV = xDirection;
    }

    public void moveWithBall(Ball ball) { this.setTranslateY(ball.getTranslateY() - RECT_W / 2);}

    public void moveRectangle() {
        this.setTranslateX(this.getTranslateX() + xV);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getRectangleId() {
        return rectagnleId;
    }
}
