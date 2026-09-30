package com.example.schoolsupermarktsimulator.objects.shelves;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class BakeryShelf extends Shelf {

    public BakeryShelf(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(BakeryShelf.class.getResourceAsStream(
                        "/assets/objects/bakery.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(240);
        sprite.setPreserveRatio(true);
    }
}
