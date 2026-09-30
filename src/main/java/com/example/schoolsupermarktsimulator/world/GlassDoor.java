package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class GlassDoor extends GameObject {

    public GlassDoor(double x, double y, double length, boolean isVertical) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(GlassDoor.class.getResourceAsStream(
                        "/assets/objects/glassdoor_" + (isVertical ? "vertical" : "horizontal") + ".png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setPreserveRatio(false);
        if (isVertical) {
            sprite.setFitHeight(length);
            sprite.setFitWidth(24);
        }
        else {
            sprite.setFitHeight(24);
            sprite.setFitWidth(length);
        }
    }
}
