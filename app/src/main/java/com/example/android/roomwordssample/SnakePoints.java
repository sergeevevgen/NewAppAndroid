package com.example.android.roomwordssample;

public class SnakePoints {
    private int positionX, positionY;

    public SnakePoints(int x, int y) {
        positionX = x;
        positionY = y;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
