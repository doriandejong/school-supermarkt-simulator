package com.example.schoolsupermarktsimulator.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ProductStand extends GameObject {

    public ProductStand(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(ProductStand.class.getResourceAsStream(
                        "/assets/objects/productstand_empty.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(64);
        sprite.setPreserveRatio(true);
    }
}