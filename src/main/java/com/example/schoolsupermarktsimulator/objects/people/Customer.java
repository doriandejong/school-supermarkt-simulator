package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.products.Product;

import java.util.List;

public class Customer extends Person {

	private double budget;

	private List<Product> groceryList;

	public void getProduct(Product p) {

	}

	public void putBackProduct(Product p) {

	}

	public boolean payGroceries() {
		return false;
	}

}
