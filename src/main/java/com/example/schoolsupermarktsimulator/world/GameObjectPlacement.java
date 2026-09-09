package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.entities.Truck;
import com.example.schoolsupermarktsimulator.objects.*;
import javafx.scene.layout.Pane;

import java.util.List;

public class GameObjectPlacement {

    private final Pane objects;

    public GameObjectPlacement() {
        objects = new Pane();

        // Magazijn items
        objects.getChildren().add(new Shelf(32, 40).getSprite());
        objects.getChildren().add(new Shelf(160, 40).getSprite());

        placeBasketStack(320, 80);
        placeBasketStack(32, 304);
        placeBasketStack(96, 304);

        objects.getChildren().add(new SaleStand(32, 338).getSprite());
        objects.getChildren().add(new SaleStand(96, 338).getSprite());
        objects.getChildren().add(new SaleStand(160, 338).getSprite());

        objects.getChildren().add(new Truck(-270, 464).getSprite());

        // Winkel (product display) items
        objects.getChildren().add(new BakeryShelf(496, 40).getSprite());

        objects.getChildren().add(new DrinkCooler(768, 40).getSprite());
        objects.getChildren().add(new DrinkCooler(816, 40).getSprite());
        objects.getChildren().add(new DrinkCooler(864, 40).getSprite());

        objects.getChildren().add(new Shelf(928, 40).getSprite());
        objects.getChildren().add(new Shelf(1056, 40).getSprite());

        // Second row of shelves
        objects.getChildren().add(new Shelf(800, 174).getSprite());
        objects.getChildren().add(new Shelf(928, 174).getSprite());
        objects.getChildren().add(new Shelf(1056, 174).getSprite());

        // Group of produce bins
        objects.getChildren().add(new ProduceBin(528, 420).getSprite());
        objects.getChildren().add(new ProduceBin(528, 468).getSprite());
        objects.getChildren().add(new ProduceBin(528, 516).getSprite());
        objects.getChildren().add(new ProduceBin(528, 564).getSprite());
        objects.getChildren().add(new ProduceBin(592, 420).getSprite());
        objects.getChildren().add(new ProduceBin(592, 468).getSprite());
        objects.getChildren().add(new ProduceBin(592, 516).getSprite());
        objects.getChildren().add(new ProduceBin(592, 564).getSprite());

        // Wall to separate checkout area from main store
        objects.getChildren().add(new BlueWall(896, 372, 368).getSprite());

        // Two cash registers with accompanying chairs
        objects.getChildren().add(new StaffChair(1050, 404).getSprite());
        objects.getChildren().add(new CashRegister(960, 436).getSprite());
        objects.getChildren().add(new StaffChair(1050, 548).getSprite());
        objects.getChildren().add(new CashRegister(960, 580).getSprite());

    }

    public Pane getView() {
        return objects;
    }

    private void placeBasketStack(double x, double y) {
        for (int i = 0; i < 5; i++) {
            objects.getChildren().add(new ProduceBasket(x, y-(i*12)).getSprite());
        }
    }
}