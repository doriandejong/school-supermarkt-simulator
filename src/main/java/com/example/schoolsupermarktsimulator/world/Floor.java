package com.example.schoolsupermarktsimulator.world;

import javafx.scene.layout.Pane;

public class Floor {

    private static final double TILE_SIZE = 80;

    private final Pane floor;

    public Floor(int columns, int rows) {

        floor = new Pane();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                double x = column * TILE_SIZE;
                double y = row * TILE_SIZE;

                boolean wall = row == 0;

                FloorTile tile = new FloorTile(x, y, wall);

                floor.getChildren().add(tile.getSprite());
            }
        }
    }

    public Pane getView() {
        return floor;
    }
}