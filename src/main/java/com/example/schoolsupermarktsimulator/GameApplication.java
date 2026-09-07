package com.example.schoolsupermarktsimulator;

import com.example.schoolsupermarktsimulator.world.GameWorld;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("STARTING GAME");

        GameWorld gameWorld = new GameWorld();

        Scene scene = new Scene(gameWorld.getView(), 1280, 720);

        stage.setTitle("School Supermarkt Simulator");
        stage.setScene(scene);
        stage.show();

        System.out.println("WINDOW SHOWN");
    }
}