package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class Shelf extends GameObject {

	private String shelfId;
	private int capacity;
	private int minimumInventory;
	private List<Product> products;
	private Warehouse warehouse;
	private Tooltip tooltip;

	public Shelf(double x, double y, String shelfId, int capacity, int minimumInventory, Warehouse warehouse) {
		super(x, y);
		this.shelfId = shelfId;
		this.capacity = capacity;
		this.minimumInventory = minimumInventory;
		this.warehouse = warehouse;
		this.products = new ArrayList<>();
	}

	//	Koppelt de tooltip aan de JavaFX sprite van dit schap.
	//	Moet worden aangeroepen zodra de 'sprite' (ImageView) in de subklasse is aangemaakt.
	protected void setupTooltip() {
		if (sprite != null) {
			tooltip = new Tooltip();
			// Optioneel: zorg dat de tooltip direct verschijnt zonder vertraging
			tooltip.setShowDelay(Duration.millis(100));
			// Koppel de tooltip aan de sprite
			Tooltip.install(sprite, tooltip);
			// Zet de initiële tekst (bijv. "0/40")
			updateTooltipText();
		}
	}

	//	Werkt de tekst van de tooltip bij naar het huidige aantal / maximale capaciteit.
	private void updateTooltipText() {
		if (tooltip != null) {
			tooltip.setText(products.size() + "/" + capacity);
		}
	}

	public int fillShelf(List<Product> newProducts) {
		int addedCount = 0;
		for (Product product : newProducts)
			if (products.size() < capacity) {
				products.add(product);
				addedCount++;
			} else {
				break; // Schap zit vol
			}
		updateTooltipText();
		return addedCount;
	}

	public Product takeProduct() {
		if (products.isEmpty()) {
			return null;
		}

		Product product = products.remove(products.size() - 1);

		// Alleen op het moment dat de drempel geraakt wordt, niet als de voorraad er al onder zit
		if (products.size() == minimumInventory && warehouse != null) {
			warehouse.requestRestock(this);
		}
		updateTooltipText();
		return product;
	}

	public int getCurrentStockCount() {
		return products.size();
	}

	public int getCapacity() {
		return capacity;
	}
}