package com.example.schoolsupermarktsimulator.world;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Wall {

    private final double x;
    private final double y;

    private final ImageView sprite;

    public Wall(double x, double y) {
        this.x = x;
        this.y = y;

        Image image = new Image(
                Objects.requireNonNull(
                        Wall.class.getResourceAsStream(
                                "/assets/background/wall_side.png"
                        )
                )
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitHeight(720);
        sprite.setPreserveRatio(true);
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