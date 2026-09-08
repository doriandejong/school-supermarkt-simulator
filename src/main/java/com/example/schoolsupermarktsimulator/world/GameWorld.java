package com.example.schoolsupermarktsimulator.world;

import javafx.scene.layout.Pane;

public class GameWorld {

    private final Pane world;

    public GameWorld() {
        world = new Pane();

        Background background = new Background(16, 9);

        world.getChildren().add(background.getView());
    }

    public Pane getView() {
        return world;
    }
}
