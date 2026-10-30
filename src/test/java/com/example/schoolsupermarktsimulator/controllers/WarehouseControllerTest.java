package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.WarehouseEmployee;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.products.RegularProduct;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarehouseControllerTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit was al gestart.
        }
    }

    // Test dat dozen van de vrachtwagen worden gescand en daarna op het winkelchap belanden.
    @Test
    void truckBoxesAreScannedOntoWarehouseShelvesAndThenOntoTheShopShelf() {
        Warehouse warehouse = new Warehouse();
        WarehouseController controller = new WarehouseController();
        controller.bind(warehouse, null, null);

        RegularShelf shop = new RegularShelf(0, 0, warehouse, RegularProduct.Kind.PASTA);
        controller.registerShopShelf(shop);
        shop.fillShelf(stock(shop, 6));

        WarehouseShelf storage = new WarehouseShelf(0, 0, "WAREHOUSE-1", warehouse);
        warehouse.addWarehouseShelf(storage);

        WarehouseEmployee employee = new WarehouseEmployee(new Node(1, 0, 0), 3, "Piet");
        controller.addWarehouseEmployee(employee);

        assertNotNull(shop.takeProduct(shop.getProducts().getFirst()));
        assertEquals(shop.getMinimumInventory(), shop.getCurrentStockCount());

        warehouse.recieveTruck(null);

        // Dozen van twee. Het magazijnschap heeft 15 plekken, dus zeven dozen (14 artikelen).
        assertEquals(19, shop.getCurrentStockCount());
        assertEquals(14, employee.getScannedProducts().size());
        assertEquals("Pasta", employee.getScannedProducts().getFirst().getName());
        assertTrue(warehouse.getStock("Pasta").isEmpty());
    }

    private static List<Product> stock(RegularShelf shelf, int amount) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            products.add(shelf.createStockItem());
        }
        return products;
    }
}
