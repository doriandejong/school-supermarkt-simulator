package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.CashRegister;
import javafx.scene.layout.Pane;

public class GameWorld {

    private final Pane world;

    public GameWorld() {
        world = new Pane();

        Floor floor = new Floor(16, 9);

        world.getChildren().add(floor.getView());

    }

    public Pane getView() {
        return world;
    }
}
