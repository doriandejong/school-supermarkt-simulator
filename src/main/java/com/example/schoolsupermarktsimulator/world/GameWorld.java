package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.nodes.Pathfinder;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;

public class GameWorld {

    private final Pane world;

    public GameWorld() {
        world = new Pane();

        Background background = new Background(16, 9);
        StoreFloorView objectPlacement = new StoreFloorView();

        world.getChildren().add(background.getView());
        world.getChildren().add(objectPlacement.getView());

        NodeMap nodeMap = new NodeMap();

        for (int i = 0; i < nodeMap.getNodes().size(); i++) {
            Customer k = new Customer(1300, 310, i, "Mapper", 512);
            k.sprite.setFill(Color.rgb((int)Math.floor(Math.random() * 250), (int)Math.floor(Math.random() * 250), (int)Math.floor(Math.random() * 250)));
            world.getChildren().add(k.sprite);
            k.setPath(Pathfinder.findPath(nodeMap.getNode(0), nodeMap.getNode(i)));
            k.startMoving();
        }
    }

    public Pane getView() {
        return world;
    }
}
