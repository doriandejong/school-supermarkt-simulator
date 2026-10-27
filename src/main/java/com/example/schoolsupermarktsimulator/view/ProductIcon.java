package com.example.schoolsupermarktsimulator.view;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public final class ProductIcon {

    private static final double CELL_WIDTH = 60;
    private static final double CELL_HEIGHT = 100;
    private static final int COLUMNS = 7;

    // Cel 24 op het spritesheet is leeg.
    private static final int[] VARIANTS = {
            0, 1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13,
            14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 25, 26, 27
    };

    private static final Image SHEET = new Image(
            Objects.requireNonNull(ProductIcon.class.getResourceAsStream(
                    "/assets/entities/spritesheet.png"
            ))
    );

    private ProductIcon() {
    }

    public static ImageView create(int variant, double x, double y, double fitHeight) {
        int index = VARIANTS[Math.floorMod(variant, VARIANTS.length)];
        int column = index % COLUMNS;
        int row = index / COLUMNS;

        ImageView icon = new ImageView(SHEET);
        icon.setViewport(new Rectangle2D(
                column * CELL_WIDTH,
                row * CELL_HEIGHT,
                CELL_WIDTH,
                CELL_HEIGHT
        ));
        icon.setFitHeight(fitHeight);
        icon.setPreserveRatio(true);
        icon.setX(x);
        icon.setY(y);
        icon.setUserData(variant);
        return icon;
    }
}
