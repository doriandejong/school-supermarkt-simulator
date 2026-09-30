package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.people.Person;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.registers.Register;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shopping.MoveableInventory;
import com.example.schoolsupermarktsimulator.view.SimulationStage;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;

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

}