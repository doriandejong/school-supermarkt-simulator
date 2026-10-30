package com.example.schoolsupermarktsimulator.objects.registers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Cashier;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingBasket;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit was al gestart.
        }
    }

    // Test dat een bezette kassa de volgende klant in de wachtrij zet.
    @Test
    void busyRegisterQueuesTheNextCustomer() {
        Node node = new Node(0, 0, 0);
        Customer first = customerWith(node, new Product("pasta", "Pasta", 1));
        Customer second = customerWith(node, new Product("jam", "Jam", 1));
        CashRegister register = new CashRegister(0, 0);
        register.assignCashier(new Cashier(node, 2, "Sara"));

        assertTrue(register.proceedToRegister(first));
        assertFalse(register.completeCheckout(second, null));
        assertEquals(1, register.getQueueLength());
        assertSame(first, register.getCurrentCustomer());
    }

    // Test dat een zelfscan bij een steekproef wacht tot een medewerker de artikelen scant.
    @Test
    void selfScanWaitsForACashierWhenTheInspectionTriggers() {
        Node node = new Node(0, 0, 0);
        Customer customer = customerWith(node, new Product("melon", "Meloen", 2.50));
        SelfScanRegister register = new SelfScanRegister(0, 0);
        register.setInspectionChance(1);

        assertFalse(register.completeCheckout(customer, null));
        assertEquals(1, customer.getAid().getProducts().size());

        Cashier inspector = new Cashier(node, 2, "Sara");
        assertTrue(register.completeCheckout(customer, inspector));
        assertEquals(1, inspector.getScannedProducts().size());
        assertTrue(customer.getAid().getProducts().isEmpty());
    }

    private static Customer customerWith(Node node, Product product) {
        Customer customer = new Customer(node, 1, "Joop");
        customer.setBudget(30);
        customer.attachAid(new ShoppingBasket());
        assertTrue(customer.getProduct(product));
        return customer;
    }
}
