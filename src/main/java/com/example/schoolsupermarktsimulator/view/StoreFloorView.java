package com.example.schoolsupermarktsimulator.view;

import com.example.schoolsupermarktsimulator.objects.*;
import com.example.schoolsupermarktsimulator.objects.registers.StaffChair;
import com.example.schoolsupermarktsimulator.objects.registers.CashRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.BakeryShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.Freezer;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.warehouse.Truck;
import javafx.scene.layout.Pane;

public class StoreFloorView {

    private final Pane objects;

    public StoreFloorView() {
        objects = new Pane();

        // Magazijn items
        objects.getChildren().add(new RegularShelf(32, 40).getSprite());
        objects.getChildren().add(new RegularShelf(160, 40).getSprite());

        placeBasketStack(320, 80);
        placeBasketStack(32, 304);
        placeBasketStack(96, 304);

        objects.getChildren().add(new SaleStand(32, 338).getSprite());
        objects.getChildren().add(new SaleStand(96, 338).getSprite());
        objects.getChildren().add(new SaleStand(160, 338).getSprite());

        objects.getChildren().add(new Truck(-270, 464).getSprite());

        // Winkel (product display) items
        objects.getChildren().add(new BakeryShelf(496, 40).getSprite());

        objects.getChildren().add(new Freezer(768, 40).getSprite());
        objects.getChildren().add(new Freezer(816, 40).getSprite());
        objects.getChildren().add(new Freezer(864, 40).getSprite());

        objects.getChildren().add(new RegularShelf(928, 40).getSprite());
        objects.getChildren().add(new RegularShelf(1056, 40).getSprite());
//        Spritesheet productSprites = new Spritesheet(928, 40, 60, 100);
//        objects.getChildren().add(productSprites.getSprite(0));
//        objects.getChildren().add(productSprites.getSprite(6));
//        objects.getChildren().add(productSprites.getSprite(14));

        // Second row of shelves
        objects.getChildren().add(new RegularShelf(800, 174).getSprite());
        objects.getChildren().add(new RegularShelf(928, 174).getSprite());
        objects.getChildren().add(new RegularShelf(1056, 174).getSprite());

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