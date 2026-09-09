package com.example.schoolsupermarktsimulator.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class BlueWall extends GameObject {

    public BlueWall(double x, double y, double length) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(CashRegister.class.getResourceAsStream(
                        "/assets/objects/blue_wall.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setPreserveRatio(false);
        sprite.setFitHeight(64);
        sprite.setFitWidth(length);
    }
}
