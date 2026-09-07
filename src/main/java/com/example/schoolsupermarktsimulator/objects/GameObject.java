package com.example.schoolsupermarktsimulator.objects;


import javafx.scene.image.ImageView;

public abstract class GameObject {

    protected double x;
    protected double y;

    protected ImageView sprite;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ImageView getSprite() {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}