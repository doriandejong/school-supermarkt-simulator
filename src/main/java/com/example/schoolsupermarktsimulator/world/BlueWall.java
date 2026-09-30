package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class BlueWall extends GameObject {

    public BlueWall(double x, double y, double length) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(BlueWall.class.getResourceAsStream(
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
