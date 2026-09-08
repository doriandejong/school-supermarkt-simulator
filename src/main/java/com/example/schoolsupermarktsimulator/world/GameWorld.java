package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.entities.Truck;
import com.example.schoolsupermarktsimulator.objects.Shelf;
import javafx.scene.layout.Pane;

public class GameWorld {

    private final Pane world;

    public GameWorld() {
        world = new Pane();

        Background background = new Background(16, 9);
        GameObjectPlacement objectPlacement = new GameObjectPlacement();

        world.getChildren().add(background.getView());
        world.getChildren().add(objectPlacement.getView());
    }

    public Pane getView() {
        return world;
    }
}
