package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.people.Person;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.registers.Register;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shopping.MoveableInventory;
import com.example.schoolsupermarktsimulator.view.SimulationStage;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupermarketController {

    private boolean isPaused;

    private Shelf[] shelf;

    private StoreFloorView storeFloorView;

    private SimulationStage simulationStage;

    private Product product;

    private Register[] register;

    private Person[] person;

    private MoveableInventory moveableInventory;

    public void startSimulation() {

    }

    public void pauseSimulation() {

    }

    public void updateSimulation() {

    }

    public List<Node> findRequiredNodes(Customer customer, List<Shelf> allShelves) {
        // Ga door alle shelves, per shelf alle producten, per product ga door de boodschappenlijst en kijk of er een match is
        return allShelves.stream()
                .filter(shelf ->
                        shelf.getProducts().stream()
                                .anyMatch(hasProduct ->
                                        customer.getGroceryList().stream()
                                                .anyMatch(wantsProduct ->
                                                        wantsProduct.getName().equals(hasProduct.getName()
                                                        )
                                                )
                                )
                )
                .map(Shelf::getShelfNode)
                .collect(Collectors.toList());
    }

}