package com.example.schoolsupermarktsimulator.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class StaffChair extends GameObject {

    public StaffChair(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(StaffChair.class.getResourceAsStream(
                        "/assets/objects/staffchair.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(48);
        sprite.setPreserveRatio(true);
    }
}