package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.controllers.SupermarketController;
import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.nodes.Pathfinder;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
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
        SupermarketController smController = new SupermarketController();

//        for (int i = 0; i < nodeMap.getNodes().size(); i++) {
//            Customer k = new Customer(nodeMap.getNode(0), i, "Mapper", 1024);
//            k.sprite.setFill(Color.rgb((int)Math.floor(Math.random() * 250), (int)Math.floor(Math.random() * 250), (int)Math.floor(Math.random() * 250)));
//            world.getChildren().add(k.sprite);
//            k.setPath(Pathfinder.findPath(k.currentNode, nodeMap.getNode(i)));
//            k.startMoving();
//        }

        Customer joop = new Customer(nodeMap.getNode(0), 666, "Joop");
        joop.sprite.setFill(Color.RED);
        world.getChildren().add(joop.sprite);
        List<Product> groceries = Arrays.asList(
                new Product("Meloen")
        );
        joop.setGroceryList(groceries);
        List<Node> requiredNodes = smController.findRequiredNodes(joop, objectPlacement.getAllShelves());
        System.out.println(requiredNodes.size() + " req nodes");
        List<Node> route = Arrays.asList( nodeMap.getNode(4), nodeMap.getNode(33), nodeMap.getNode(56), nodeMap.getNode(44));
        joop.setWalkingRoute(route);
    }

    public Pane getView() {
        return world;
    }
}
