package com.example.schoolsupermarktsimulator.view;

import com.example.schoolsupermarktsimulator.objects.*;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.registers.StaffChair;
import com.example.schoolsupermarktsimulator.objects.registers.CashRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.BakeryShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.Freezer;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.warehouse.Truck;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreFloorView {

    // Zelfde startvoorraad als in ShelfTest: na één klik blijft er 10 over.
    private static final int START_STOCK = 11;

    private final Pane objects;
    private final Map<Shelf, List<ImageView>> stockIcons = new HashMap<>();

    public StoreFloorView() {
        objects = new Pane();
        Truck truck = new Truck(-200, 464);
        Warehouse warehouse = new Warehouse();
        warehouse.setDeliveryTruck(truck);

        // Magazijn items: leeg tot de vrachtwagen ze vult
        WarehouseShelf warehouseLeft = new WarehouseShelf(32, 40, "WAREHOUSE-1", warehouse);
        WarehouseShelf warehouseRight = new WarehouseShelf(160, 40, "WAREHOUSE-2", warehouse);
        warehouse.addWarehouseShelf(warehouseLeft);
        warehouse.addWarehouseShelf(warehouseRight);
        addShelf(warehouseLeft, false);
        addShelf(warehouseRight, false);

        placeBasketStack(320, 80);
        placeBasketStack(32, 304);
        placeBasketStack(96, 304);

        objects.getChildren().add(new SaleStand(32, 338).getSprite());
        objects.getChildren().add(new SaleStand(96, 338).getSprite());
        objects.getChildren().add(new SaleStand(160, 338).getSprite());

        objects.getChildren().add(truck.getSprite());

        // Winkel (product display) items
        addShelf(new BakeryShelf(496, 40, warehouse));

        addShelf(new Freezer(768, 40, warehouse));
        addShelf(new Freezer(816, 40, warehouse));
        addShelf(new Freezer(864, 40, warehouse));

        addShelf(new RegularShelf(928, 40, warehouse));
        addShelf(new RegularShelf(1056, 40, warehouse));
//        Spritesheet productSprites = new Spritesheet(928, 40, 60, 100);
//        objects.getChildren().add(productSprites.getSprite(0));
//        objects.getChildren().add(productSprites.getSprite(6));
//        objects.getChildren().add(productSprites.getSprite(14));

        // Second row of shelves
        addShelf(new RegularShelf(800, 174, warehouse));
        addShelf(new RegularShelf(928, 174, warehouse));
        addShelf(new RegularShelf(1056, 174, warehouse));

        // Group of produce bins
        objects.getChildren().add(new ProduceBin(528, 420).getSprite());
        objects.getChildren().add(new ProduceBin(528, 468).getSprite());
        objects.getChildren().add(new ProduceBin(528, 516).getSprite());
        objects.getChildren().add(new ProduceBin(528, 564).getSprite());
        objects.getChildren().add(new ProduceBin(592, 420).getSprite());
        objects.getChildren().add(new ProduceBin(592, 468).getSprite());
        objects.getChildren().add(new ProduceBin(592, 516).getSprite());
        objects.getChildren().add(new ProduceBin(592, 564).getSprite());



        // Two cash registers with accompanying chairs
        objects.getChildren().add(new StaffChair(1050, 404).getSprite());
        objects.getChildren().add(new CashRegister(960, 436).getSprite());
        objects.getChildren().add(new StaffChair(1050, 548).getSprite());
        objects.getChildren().add(new CashRegister(960, 580).getSprite());

    }

    public Pane getView() {
        return objects;
    }

    private void addShelf(Shelf shelf) {
        addShelf(shelf, true);
    }

    private void addShelf(Shelf shelf, boolean withStartStock) {
        if (withStartStock) {
            List<Product> products = new ArrayList<>();
            for (int i = 0; i < START_STOCK; i++) {
                products.add(new Product("Product " + (i + 1)));
            }
            shelf.fillShelf(products);
        }

        ImageView sprite = shelf.getSprite();
        sprite.setPickOnBounds(true);
        sprite.setCursor(Cursor.HAND);
        sprite.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                takeItemFrom(shelf);
            }
        });
        objects.getChildren().add(sprite);
        if (shelf instanceof WarehouseShelf warehouseShelf) {
            objects.getChildren().addAll(warehouseShelf.getStockSlots());
        } else {
            showStockOnShelf(shelf);
        }
    }

    private void showStockOnShelf(Shelf shelf) {
        ImageView sprite = shelf.getSprite();
        double shelfX = sprite.getX();
        double shelfY = sprite.getY();
        double shelfW = sprite.getFitWidth();
        double shelfH = shelfW * sprite.getImage().getHeight() / sprite.getImage().getWidth();

        double padding = 8;
        double iconHeight = Math.min(28, (shelfH - padding * 2) / 3.2);
        double iconWidth = iconHeight * 0.62;
        int columns = Math.max(1, Math.min(4, (int) ((shelfW - padding * 2) / (iconWidth + 4))));
        int rows = Math.max(1, (int) ((shelfH - padding * 2) / (iconHeight + 4)));
        int stock = shelf.getCurrentStockCount();
        // Op een breed schap passen alle producten, zodat een klik er echt één weghaalt.
        while (shelfW >= 100 && columns * rows < stock && iconHeight > 16) {
            iconHeight -= 2;
            iconWidth = iconHeight * 0.62;
            columns = Math.max(1, Math.min(4, (int) ((shelfW - padding * 2) / (iconWidth + 3))));
            rows = Math.max(1, (int) ((shelfH - padding * 2) / (iconHeight + 3)));
        }
        int shown = Math.min(stock, columns * rows);
        if (shown == 0) {
            stockIcons.put(shelf, new ArrayList<>());
            return;
        }

        int usedRows = (int) Math.ceil(shown / (double) columns);
        double gridWidth = columns * (iconWidth + 4) - 4;
        double gridHeight = usedRows * (iconHeight + 4) - 4;
        double originX = shelfX + (shelfW - gridWidth) / 2;
        double originY = shelfY + (shelfH - gridHeight) / 2;

        List<ImageView> icons = new ArrayList<>();
        for (int i = 0; i < shown; i++) {
            int column = i % columns;
            int row = i / columns;
            ImageView icon = ProductIcon.create(
                    i,
                    originX + column * (iconWidth + 4),
                    originY + row * (iconHeight + 4),
                    iconHeight
            );
            icon.setMouseTransparent(true);
            icons.add(icon);
            objects.getChildren().add(icon);
        }
        stockIcons.put(shelf, icons);
    }

    private void takeItemFrom(Shelf shelf) {
        if (shelf instanceof WarehouseShelf warehouseShelf) {
            int index = shelf.getCurrentStockCount() - 1;
            if (shelf.takeProduct() == null) {
                return;
            }
            ImageView flying = warehouseShelf.copySlot(index);
            objects.getChildren().add(flying);
            flying.toFront();
            animateOut(flying);
            return;
        }

        Product taken = shelf.takeProduct();
        if (taken == null) {
            return;
        }

        List<ImageView> icons = stockIcons.getOrDefault(shelf, List.of());
        ImageView flying;
        if (icons.size() > shelf.getCurrentStockCount()) {
            flying = icons.remove(icons.size() - 1);
        } else {
            double x = shelf.getX();
            double y = shelf.getY();
            double fitHeight = 28;
            int variant = 0;
            if (!icons.isEmpty()) {
                ImageView source = icons.get(icons.size() - 1);
                x = source.getX();
                y = source.getY();
                fitHeight = source.getFitHeight();
                if (source.getUserData() instanceof Integer stored) {
                    variant = stored;
                }
            }
            flying = ProductIcon.create(variant, x, y, fitHeight);
            flying.setMouseTransparent(true);
            objects.getChildren().add(flying);
        }

        flying.toFront();
        animateOut(flying);
    }

    private void animateOut(ImageView item) {
        TranslateTransition move = new TranslateTransition(Duration.millis(480), item);
        move.setByX(10);
        move.setByY(64);

        ScaleTransition scale = new ScaleTransition(Duration.millis(480), item);
        scale.setToX(1.8);
        scale.setToY(1.8);

        FadeTransition fade = new FadeTransition(Duration.millis(200), item);
        fade.setDelay(Duration.millis(300));
        fade.setFromValue(1);
        fade.setToValue(0);

        ParallelTransition leave = new ParallelTransition(move, scale, fade);
        leave.setOnFinished(event -> objects.getChildren().remove(item));
        leave.play();
    }

    private void placeBasketStack(double x, double y) {
        for (int i = 0; i < 5; i++) {
            objects.getChildren().add(new ProduceBasket(x, y-(i*12)).getSprite());
        }
    }
}