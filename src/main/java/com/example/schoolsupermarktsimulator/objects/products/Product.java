package com.example.schoolsupermarktsimulator.objects.products;

/**
 * Een verkoopartikel met een naam en een prijs.
 * Schappen, manden en kassa's herkennen een artikel aan de naam.
 */
public class Product {

	private String id;
	private String name;
	private double price;

	/**
	 * Maakt een artikel met alleen een naam. De prijs is dan 0.
	 * Handig voor een boodschappenlijst, waar alleen de naam hoeft te kloppen.
	 *
	 * @param name naam waarmee dit artikel op een schap gezocht wordt
	 */
	public Product(String name) {
		this.name = name;
	}

	/**
	 * Maakt een artikel met id, naam en prijs.
	 *
	 * @param id unieke code, mag null zijn
	 * @param name naam waarmee dit artikel op een schap gezocht wordt
	 * @param price verkoopprijs in euro
	 */
	public Product(String id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	/** @return de code van dit artikel, of null als die niet gezet is */
	public String getId() {
		return id;
	}

	/** @return de naam waarmee schappen en lijsten dit artikel vergelijken */
	public String getName() {
		return name;
	}

	/** @return de verkoopprijs in euro */
	public double getPrice() {
		return price;
	}
}
