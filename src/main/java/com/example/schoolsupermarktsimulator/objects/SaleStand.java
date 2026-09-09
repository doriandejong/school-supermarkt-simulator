package com.example.schoolsupermarktsimulator.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SaleStand extends GameObject {

    public SaleStand(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(SaleStand.class.getResourceAsStream(
                        "/assets/objects/salestand_empty.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(64);
        sprite.setPreserveRatio(true);
    }
}