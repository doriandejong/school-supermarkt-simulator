package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.Shelf;
import javafx.scene.layout.Pane;

public class GameWorld {

    private final Pane world;

    public GameWorld() {
        world = new Pane();

        Background background = new Background(16, 9);

        world.getChildren().add(background.getView());
        Shelf emptyShelf = new Shelf(600, 300);
        world.getChildren().add(emptyShelf.getSprite());
    }

    public Pane getView() {
        return world;
    }
}
