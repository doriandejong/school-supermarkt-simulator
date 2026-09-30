package com.example.schoolsupermarktsimulator.objects.warehouse;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import com.example.schoolsupermarktsimulator.objects.shelves.BakeryShelf;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Truck extends GameObject {

    public Truck(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(Truck.class.getResourceAsStream(
                        "/assets/entities/truck.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitHeight(240);
        sprite.setPreserveRatio(true);
    }
}
