package com.example.pong;

import javafx.scene.shape.Circle;
import java.util.Random;

public class Ball extends Circle {

    private Random random;
    public int xV;
    public int yV;
    private int speed = 2;

    Ball(int radius,int APP_W,int APP_H) {
        super(radius);
        this.setTranslateX(APP_W);
        this.setTranslateY(APP_H);

        random = new Random();
        int randomXDirection = random.nextInt(2);
        if(randomXDirection == 0)
            randomXDirection--;
        setX(randomXDirection * speed);

        int randomYDirection = random.nextInt(2);
        if(randomYDirection == 0)
            randomYDirection--;
        setY(randomYDirection * speed);
    }

    public void setX(int direction) {
        xV = direction;
    }
    public void setY(int direction) {
        yV = direction;
    }

    public int getX() {
        return this.xV;
    }

    public int getY() {
        return this.yV;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void moveBall() {
        this.setTranslateX(this.getTranslateX() + xV);
        this.setTranslateY(this.getTranslateY() + yV);
    }
}
