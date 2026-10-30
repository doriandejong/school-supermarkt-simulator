package com.example.schoolsupermarktsimulator.objects.shopping;

import com.example.schoolsupermarktsimulator.objects.products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Voorraad die een persoon meeneemt, zoals een mand, een wagen of een vulkar.
 * Subklassen kiezen alleen de maximale capaciteit; het toevoegen en weghalen zit hier.
 */
public class MoveableInventory {

	private final int capacity;
	private final List<Product> products = new ArrayList<>();

	/**
	 * @param capacity hoeveel artikelen er maximaal in passen, minstens 1
	 */
	public MoveableInventory(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Capaciteit moet minstens 1 zijn.");
		}
		this.capacity = capacity;
	}

	/**
	 * Legt een artikel erbij zolang er nog ruimte is.
	 *
	 * @param product het artikel dat erbij moet
	 * @return true als het artikel erin past en is toegevoegd
	 */
	public boolean addProduct(Product product) {
		if (product == null || products.size() >= capacity) {
			return false;
		}
		products.add(product);
		return true;
	}

	/**
	 * Haalt precies dit exemplaar eruit.
	 *
	 * @param product het exemplaar dat uit de voorraad moet
	 * @return true als dat exemplaar erin zat
	 */
	public boolean removeProduct(Product product) {
		return products.remove(product);
	}

	/** Maakt de voorraad leeg, bijvoorbeeld na het betalen. */
	public void clear() {
		products.clear();
	}

	/** @return true als er niets meer bij past */
	public boolean isFull() {
		return products.size() >= capacity;
	}

	/** @return hoeveel artikelen er nog bij passen */
	public int remainingSpace() {
		return capacity - products.size();
	}

	/** @return het maximum dat in de constructor is gekozen */
	public int getCapacity() {
		return capacity;
	}

	/** @return de artikelen die er nu in zitten; de lijst zelf is niet aanpasbaar */
	public List<Product> getProducts() {
		return List.copyOf(products);
	}
}
