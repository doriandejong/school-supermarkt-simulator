package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.products.Product;

import java.util.List;

public class Customer extends Person {

	private double budget;

	private List<Product> groceryList;

	public Customer(double x, double y, int id, String name) {
		super(x, y, id, name);
	}

	public Customer(double x, double y, int id, String name, double speed) {
		super(x, y, id, name, speed);
	}

	public void getProduct(Product p) {

	}

	public void putBackProduct(Product p) {

	}

	public boolean payGroceries() {
		return false;
	}

}
