package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;

import java.util.List;

public class Customer extends Person {

	private double budget;

	private List<Product> groceryList;

	public Customer(Node startNode, int id, String name) {
		super(startNode, id, name);
	}

	public Customer(Node startNode, int id, String name, double speed) {
		super(startNode, id, name, speed);
	}

	public List<Product> getGroceryList() {
		return this.groceryList;
	}

	public void setGroceryList(List<Product> groceryList) {
		this.groceryList = groceryList;
	}

	public void getProduct(Product p) {

	}

	public void putBackProduct(Product p) {

	}

	public boolean payGroceries() {
		return false;
	}

}
