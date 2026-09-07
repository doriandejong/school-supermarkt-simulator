package com.example.schoolsupermarktsimulator.world;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.Random;

public class FloorTile {

    private static final double SIZE = 80;

    private static final String[] TEXTURES = {
            "/assets/floor/floor_1.png",
            "/assets/floor/floor_2.png",
            "/assets/floor/floor_3.png",
            "/assets/floor/floor_4.png",
    };

    private static final String WALL_TEXTURE =
            "/assets/floor/wall_1.png";

    private static final Random RANDOM = new Random();

    private final ImageView sprite;

    public FloorTile(double x, double y, boolean wall) {

        String texture;

        if (wall) {
            texture = WALL_TEXTURE;
        } else {
            texture = TEXTURES[RANDOM.nextInt(TEXTURES.length)];
        }

        Image image = new Image(
                Objects.requireNonNull(
                        FloorTile.class.getResourceAsStream(texture)
                )
        );

        sprite = new ImageView(image);

        sprite.setFitWidth(SIZE);
        sprite.setFitHeight(SIZE);

        sprite.setX(x);
        sprite.setY(y);
    }

    public ImageView getSprite() {
        return sprite;
    }
}