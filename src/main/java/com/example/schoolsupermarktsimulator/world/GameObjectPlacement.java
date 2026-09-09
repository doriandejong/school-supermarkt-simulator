package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.entities.Truck;
import com.example.schoolsupermarktsimulator.objects.Shelf;
import javafx.scene.layout.Pane;

public class GameObjectPlacement {

    private final Pane objects;

    public GameObjectPlacement() {
        objects = new Pane();

        // Magazijn items
        objects.getChildren().add(new Shelf(32, 20).getSprite());
        objects.getChildren().add(new Shelf(224, 20).getSprite());
        objects.getChildren().add(new Truck(-270, 430).getSprite());

    }

    public Pane getView() {
        return objects;
    }
}