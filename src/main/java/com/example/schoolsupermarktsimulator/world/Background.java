package com.example.schoolsupermarktsimulator.world;

import javafx.scene.layout.Pane;

public class Background {

    private static final double TILE_SIZE = 80;

    private final Pane background;

    public Background(int columns, int rows) {

        background = new Pane();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                double x = column * TILE_SIZE;
                double y = row * TILE_SIZE;

                boolean wall = row == 0;

                FloorTile tile = new FloorTile(x, y, wall);

                background.getChildren().add(tile.getSprite());
            }
        }

        // Prebuild doors, walls will render on top of them
        background.getChildren().add(new GlassDoor(396, 208, 128, true).getSprite());
        background.getChildren().add(new GlassDoor(1260, 240, 128, true).getSprite());
        background.getChildren().add(new GlassDoor(1260, 528, 128, true).getSprite());

        // Wall to separate checkout area from main store
        background.getChildren().add(new BlueWall(896, 372, 368).getSprite());

        // Show walls
        background.getChildren().add(new Wall(400, 0).getSprite());
        background.getChildren().add(new Wall(0, 0).getSprite());
        background.getChildren().add(new Wall(1264, 0).getSprite());
    }

    public Pane getView() {
        return background;
    }
}