package com.example.schoolsupermarktsimulator.objects.shopping;

/**
 * Winkelwagen. Zelfde gedrag als een mand, maar met een hoger maximum.
 */
public class ShoppingCart extends MoveableInventory {

	/** Een wagen raakt vol na twintig artikelen. */
	public static final int MAX_CAPACITY = 20;

	public ShoppingCart() {
		super(MAX_CAPACITY);
	}
}
