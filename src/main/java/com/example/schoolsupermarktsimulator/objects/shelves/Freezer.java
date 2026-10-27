package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Freezer extends Shelf {

    public Freezer(double x, double y, Warehouse warehouse) {
        super(x, y, "REGULAR-1", 40, 10, warehouse);

        Image image = new Image(
                Objects.requireNonNull(Freezer.class.getResourceAsStream(
                        "/assets/objects/drinkcooler_empty.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(48);
        sprite.setPreserveRatio(true);

        setupTooltip();
    }
}
