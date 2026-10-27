package com.example.schoolsupermarktsimulator.objects.products;

public class Product {

	private String id;
	private String name;
	private double price;

	// Constructor voor snel/eenvoudig aanmaken (bijv. tijdens testen)
	public Product(String name) {
		this.name = name;
	}

	// Uitgebreide constructor voor alle eigenschappen
	public Product(String id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
}


