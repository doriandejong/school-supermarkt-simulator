package com.example.schoolsupermarktsimulator.objects.warehouse;

import com.example.schoolsupermarktsimulator.controllers.WarehouseController;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;

import java.util.ArrayList;
import java.util.List;

/**
 * De voorraadruimte achter de winkel: magazijnschappen en de vrachtwagen die daar levert.
 * Bestellen, dozen sjouwen en winkelschappen bijvullen doet de {@link WarehouseController}.
 * Deze klasse bewaart alleen de schappen en stuurt die aanvragen door.
 */
public class Warehouse {

	private int capacity;
	private final List<Product> stock = new ArrayList<>();
	private Truck deliveryTruck;
	private final List<WarehouseShelf> warehouseShelves = new ArrayList<>();
	private WarehouseController controller;

	/**
	 * Koppel de controller die vrachtwagen, dozen en bijvullen afhandelt.
	 *
	 * @param controller de magazijncontroller van deze winkel
	 */
	public void setController(WarehouseController controller) {
		this.controller = controller;
	}

	/**
	 * @param deliveryTruck de vrachtwagen die nieuwe dozen brengt
	 */
	public void setDeliveryTruck(Truck deliveryTruck) {
		this.deliveryTruck = deliveryTruck;
	}

	/** @return de vrachtwagen, of null als er nog geen gekoppeld is */
	public Truck getDeliveryTruck() {
		return deliveryTruck;
	}

	/**
	 * Registreer een magazijnschap waar dozen op gezet kunnen worden.
	 *
	 * @param shelf schap in het magazijn
	 */
	public void addWarehouseShelf(WarehouseShelf shelf) {
		warehouseShelves.add(shelf);
	}

	/** @return de magazijnschappen, in de volgorde waarin ze zijn toegevoegd */
	public List<WarehouseShelf> getWarehouseShelves() {
		return List.copyOf(warehouseShelves);
	}

	/**
	 * De vrachtwagen is er. De spelling blijft zoals in de oorspronkelijke code;
	 * het werk gebeurt in {@link WarehouseController#receiveTruck(Truck)}.
	 *
	 * @param truck de aangekomen vrachtwagen
	 */
	public void recieveTruck(Truck truck) {
		if (controller != null) {
			controller.receiveTruck(truck);
		}
	}

	/**
	 * Een schap zit op zijn minimum. De controller vult het vanuit het magazijn,
	 * of bestelt een vrachtwagen als die voorraad er niet is.
	 *
	 * @param shelf het schap dat aangevuld moet worden
	 */
	public void requestRestock(Shelf shelf) {
		if (controller != null) {
			controller.requestRestock(shelf);
		}
	}

	/**
	 * Zoekt voorraad op id of naam, in de magazijnschappen en in de losse lijst.
	 *
	 * @param productId id of naam van het artikel
	 * @return de gevonden exemplaren; lege lijst als de controller er niet is
	 */
	public List<Product> getStock(String productId) {
		if (controller != null) {
			return controller.getStock(productId);
		}
		return List.copyOf(stock);
	}

	/** @return de losse voorraad die niet op een schap ligt */
	public List<Product> getLooseStock() {
		return stock;
	}

	/** @return de totale capaciteit als die gezet is */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity hoeveel artikelen het magazijn in totaal kan hebben
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
