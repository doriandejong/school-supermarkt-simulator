package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Cashier;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.products.RegularProduct;
import com.example.schoolsupermarktsimulator.objects.registers.CashRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingBasket;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoreFlowTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit was al gestart.
        }
    }

    // Test de keten magazijn, schap, klant en kassa: bijvullen, pakken, scannen en betalen.
    @Test
    void customerPaysForAnItemThatWasRestockedFromTheWarehouse() {
        Warehouse warehouse = new Warehouse();
        WarehouseController controller = new WarehouseController();
        controller.bind(warehouse, null, null);

        RegularShelf shop = new RegularShelf(0, 0, warehouse, RegularProduct.Kind.PASTA);
        controller.registerShopShelf(shop);
        shop.fillShelf(stock(shop, 6));

        WarehouseShelf storage = new WarehouseShelf(0, 0, "WAREHOUSE-1", warehouse);
        warehouse.addWarehouseShelf(storage);
        storage.fillShelf(stock(shop, 10));

        Customer customer = new Customer(new Node(0, 0, 0), 1, "Joop");
        customer.setBudget(30);
        customer.attachAid(new ShoppingBasket());
        customer.setGroceryList(List.of(new Product("Pasta")));

        assertNotNull(customer.takeFromShelf(shop));
        assertEquals(15, shop.getCurrentStockCount());
        assertEquals(0, storage.getCurrentStockCount());

        CashRegister register = new CashRegister(0, 0);
        Cashier cashier = new Cashier(new Node(1, 1, 0), 2, "Sara");
        register.assignCashier(cashier);

        assertTrue(register.completeCheckout(customer, null));
        assertEquals(1, cashier.getScannedProducts().size());
        assertEquals("Pasta", cashier.getScannedProducts().getFirst().getName());
        assertTrue(customer.getAid().getProducts().isEmpty());
        assertEquals(0, customer.getSpent(), 0.001);
    }

    private static List<Product> stock(RegularShelf shelf, int amount) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            products.add(shelf.createStockItem());
        }
        return products;
    }
}
