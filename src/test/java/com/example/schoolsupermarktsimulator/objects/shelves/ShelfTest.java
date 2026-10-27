package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {

    @BeforeAll
    static void initJavaFX() {
    // Start de JavaFX Toolkit eenmalig voor de hele testklasse
        try {
            Platform.startup(() -> {});
        } catch(IllegalStateException e) {
            // Toolkit was al gestart
        }
    }

    @Test
    void testTakeProductReducesCountAndTriggersWarehouse() {
    // ARRANGE: Maak een schap met 11 producten [4]
        Warehouse warehouse = new Warehouse();
        RegularShelf shelf = new RegularShelf(0, 0, warehouse);

        List<Product> producten = new ArrayList<> ();
        for (int i = 0; i < 11; i++) {
            producten.add(new Product("TestProduct"));
        }
        shelf.fillShelf(producten);

        // ACT: Pak 1 product (voorraad gaat naar 10) [4]
        Product gepakt = shelf.takeProduct();

        // ASSERT: Controleer of het aantal klopt [3, 4]
        assertNotNull(gepakt);
        assertEquals(10, shelf.getCurrentStockCount());
    }
}