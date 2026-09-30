package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.GameObject;

public class Shelf extends GameObject {

	private String shelfId;

	private int capacity;

	private int minimumInventory;

	public Shelf(double x, double y) {
		super(x, y);
	}

//	public int fillShelf(List<Product> products) {
//		return 0;
//	}
//
//	public Product takeProduct() {
//		return null;
//	}

}
