package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.products.RegularProduct;
import com.example.schoolsupermarktsimulator.objects.shelves.RegularShelf;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingBasket;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerShelfTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit was al gestart.
        }
    }

    // Test dat een te duur artikel terug op het schap gaat als het budget op is.
    @Test
    void productGoesBackOnTheShelfWhenTheBudgetIsTooLow() {
        Node door = new Node(0, 0, 0);
        Customer customer = new Customer(door, 1, "Joop");
        customer.setBudget(0);
        customer.attachAid(new ShoppingBasket());
        customer.setGroceryList(List.of(new Product("Pasta")));

        Warehouse warehouse = new Warehouse();
        RegularShelf shelf = new RegularShelf(0, 0, warehouse, RegularProduct.Kind.PASTA);
        shelf.fillShelf(stock(shelf, 10));

        assertNull(customer.takeFromShelf(shelf));
        assertEquals(10, shelf.getCurrentStockCount());
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
