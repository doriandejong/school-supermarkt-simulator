package com.example.schoolsupermarktsimulator.objects.shelves;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class RegularShelf extends Shelf {

    public RegularShelf(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(RegularShelf.class.getResourceAsStream(
                        "/assets/objects/shelf_empty.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(128);
        sprite.setPreserveRatio(true);
    }
}
