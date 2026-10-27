package com.example.schoolsupermarktsimulator.view;

import com.example.schoolsupermarktsimulator.objects.*;
import com.example.schoolsupermarktsimulator.objects.products.FreshProduce;
import com.example.schoolsupermarktsimulator.objects.products.FrozenProduct;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.products.RegularProduct;
import com.example.schoolsupermarktsimulator.objects.registers.StaffChair;
import com.example.schoolsupermarktsimulator.objects.registers.CashRegister;
import com.example.schoolsupermarktsimulator.objects.registers.SelfScanRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.BakeryShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.Freezer;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.VegetableShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.warehouse.BoxStack;
import com.example.schoolsupermarktsimulator.objects.warehouse.Truck;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

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
        BoxStack boxStack = new BoxStack(256, 592);
        truck.setBoxStack(boxStack);
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

        objects.getChildren().addAll(boxStack.getSprites());
        objects.getChildren().add(truck.getSprite());

        // Winkel (product display) items
        objects.getChildren().add(new BakeryShelf(496, 40, warehouse).getSprite());

        // Koelvitrines. Zonder soort kiest de vitrine zelf cola, sap of frisdrank.
        addFreezer(768, 40, warehouse, FrozenProduct.Kind.COLA);
        addFreezer(816, 40, warehouse, FrozenProduct.Kind.JUICE);
        addFreezer(864, 40, warehouse);

        // Gewone schappen. Zonder soort kiest het schap zelf een product.
        addRegularShelf(928, 40, warehouse, RegularProduct.Kind.SNACKS);
        addRegularShelf(1056, 40, warehouse, RegularProduct.Kind.EGGS);
//        Spritesheet productSprites = new Spritesheet(928, 40, 60, 100);
//        objects.getChildren().add(productSprites.getSprite(0));
//        objects.getChildren().add(productSprites.getSprite(6));
//        objects.getChildren().add(productSprites.getSprite(14));

        // Second row of shelves
        addRegularShelf(800, 174, warehouse, RegularProduct.Kind.PASTA);
        addRegularShelf(928, 174, warehouse, RegularProduct.Kind.CANDY);
        addRegularShelf(1056, 174, warehouse, RegularProduct.Kind.JAM);

        // Groenteschappen. Zonder soort kiest het schap zelf meloen, kool of pompoen.
        addVegetableShelf(528, 420, warehouse, FreshProduce.Kind.MELON);
        addVegetableShelf(528, 468, warehouse, FreshProduce.Kind.CABBAGE);
        addVegetableShelf(528, 516, warehouse, FreshProduce.Kind.MELON);
        addVegetableShelf(528, 564, warehouse, FreshProduce.Kind.PUMPKIN);
        addVegetableShelf(592, 420, warehouse, FreshProduce.Kind.MELON);
        addVegetableShelf(592, 468, warehouse, FreshProduce.Kind.CABBAGE);
        addVegetableShelf(592, 516, warehouse, FreshProduce.Kind.MELON);
        addVegetableShelf(592, 564, warehouse, FreshProduce.Kind.PUMPKIN);



        // Two cash registers with accompanying chairs
        objects.getChildren().add(new StaffChair(1050, 404).getSprite());
        objects.getChildren().add(new CashRegister(960, 436).getSprite());
        objects.getChildren().add(new SelfScanRegister(978, 591).getSprite());

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
        } else if (!(shelf instanceof VegetableShelf) && !(shelf instanceof Freezer) && !(shelf instanceof RegularShelf)) {
            showStockOnShelf(shelf);
        }
    }

    private void addRegularShelf(double x, double y, Warehouse warehouse) {
        addRegularShelf(x, y, warehouse, null);
    }

    private void addRegularShelf(double x, double y, Warehouse warehouse, RegularProduct.Kind kind) {
        RegularShelf shelf = kind == null
                ? new RegularShelf(x, y, warehouse)
                : new RegularShelf(x, y, warehouse, kind);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < START_STOCK; i++) {
            products.add(new RegularProduct(shelf.getKind()));
        }
        shelf.fillShelf(products);
        addShelf(shelf, false);
    }

    private void addFreezer(double x, double y, Warehouse warehouse) {
        addFreezer(x, y, warehouse, null);
    }

    private void addFreezer(double x, double y, Warehouse warehouse, FrozenProduct.Kind kind) {
        Freezer freezer = kind == null
                ? new Freezer(x, y, warehouse)
                : new Freezer(x, y, warehouse, kind);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < START_STOCK; i++) {
            products.add(new FrozenProduct(freezer.getKind()));
        }
        freezer.fillShelf(products);
        addShelf(freezer, false);
    }

    private void addVegetableShelf(double x, double y, Warehouse warehouse) {
        addVegetableShelf(x, y, warehouse, null);
    }

    private void addVegetableShelf(double x, double y, Warehouse warehouse, FreshProduce.Kind kind) {
        VegetableShelf shelf = kind == null
                ? new VegetableShelf(x, y, warehouse)
                : new VegetableShelf(x, y, warehouse, kind);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < START_STOCK; i++) {
            products.add(new FreshProduce(shelf.getKind()));
        }
        shelf.fillShelf(products);
        addShelf(shelf, false);
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
        if (shelf.takeProduct() == null) {
            return;
        }

        List<ImageView> icons = stockIcons.get(shelf);
        if (icons != null && icons.size() > shelf.getCurrentStockCount()) {
            objects.getChildren().remove(icons.remove(icons.size() - 1));
        }
    }

    private void placeBasketStack(double x, double y) {
        for (int i = 0; i < 5; i++) {
            objects.getChildren().add(new ProduceBasket(x, y-(i*12)).getSprite());
        }
    }
}