package com.example.schoolsupermarktsimulator;

import com.example.schoolsupermarktsimulator.world.GameWorld;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

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