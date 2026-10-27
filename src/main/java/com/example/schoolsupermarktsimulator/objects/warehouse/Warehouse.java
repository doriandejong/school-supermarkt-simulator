package com.example.schoolsupermarktsimulator.objects.warehouse;

import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {

	private int capacity;
	private List<Product> stock = new ArrayList<>();
	private Truck deliveryTruck;
	private final List<WarehouseShelf> warehouseShelves = new ArrayList<>();

	public void setDeliveryTruck(Truck deliveryTruck) {
		this.deliveryTruck = deliveryTruck;
	}

	public void addWarehouseShelf(WarehouseShelf shelf) {
		warehouseShelves.add(shelf);
	}

	public void recieveTruck(Truck t) {
		System.out.println("Vrachtwagen is aangekomen en levert nieuwe producten.");
		for (WarehouseShelf shelf : warehouseShelves) {
			shelf.restockFromTruck();
		}
	}

	public void requestRestock(Shelf shelf) {
		if (!hasRoomInWarehouse()) {
			return;
		}
		System.out.println("Drempelwaarde bereikt voor schap! Er wordt een truck besteld.");
		if (deliveryTruck != null) {
			deliveryTruck.deliver(this::hasRoomInWarehouse, () -> recieveTruck(deliveryTruck));
		}
	}

	private boolean hasRoomInWarehouse() {
		for (WarehouseShelf warehouseShelf : warehouseShelves) {
			if (warehouseShelf.getCurrentStockCount() < warehouseShelf.getCapacity()) {
				return true;
			}
		}
		return false;
	}

	public List<Product> getStock(String productId) {
		return null;
	}
}