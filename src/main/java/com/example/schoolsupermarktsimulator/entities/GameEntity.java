package com.example.schoolsupermarktsimulator.entities;


import javafx.scene.image.ImageView;

public abstract class GameEntity {

    protected double x;
    protected double y;

    protected ImageView sprite;

    public GameEntity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ImageView getSprite(int index) {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}