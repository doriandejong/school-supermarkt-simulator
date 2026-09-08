package com.example.schoolsupermarktsimulator.world;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.Random;

public class FloorTile {

    private static final double SIZE = 80;

    private static final String[] FLOOR_TEXTURES_NORMAL = {
            "/assets/background/floor_1.png",
            "/assets/background/floor_2.png",
            "/assets/background/floor_3.png",
            "/assets/background/floor_4.png",
    };

    private static final String[] FLOOR_TEXTURES_GREY = {
            "/assets/background/floor_alt_1.png",
            "/assets/background/floor_alt_2.png",
            "/assets/background/floor_alt_3.png",
            "/assets/background/floor_alt_4.png",
    };

    private static final String WALL_TEXTURE =
            "/assets/background/wall_1.png";

    private static final Random RANDOM = new Random();

    private final ImageView sprite;

    public FloorTile(double x, double y, boolean wall) {

        String texture;

        if (wall) {
            texture = WALL_TEXTURE;
        } else if (x < SIZE * 4) {
            texture = FLOOR_TEXTURES_GREY[
                    RANDOM.nextInt(FLOOR_TEXTURES_GREY.length)
                    ];
        } else {
            texture = FLOOR_TEXTURES_NORMAL[
                    RANDOM.nextInt(FLOOR_TEXTURES_NORMAL.length)
                    ];
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