package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class RegularShelf extends Shelf {

    public RegularShelf(double x, double y, Warehouse warehouse) {

        // Geef x, y, id, capaciteit (bijv. 20) en drempelwaarde (bijv. 5) mee
        super(x, y, "REGULAR-1", 20, 5, warehouse);

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

        // Koppel de tooltip nu de sprite klaar is
        setupTooltip();
    }
}