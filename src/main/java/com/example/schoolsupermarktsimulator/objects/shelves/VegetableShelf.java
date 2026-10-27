package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;

public class VegetableShelf extends Shelf {

    public VegetableShelf(double x, double y, Warehouse warehouse) {
        super(x, y, "REGULAR-1", 40, 10, warehouse);
    }
}
