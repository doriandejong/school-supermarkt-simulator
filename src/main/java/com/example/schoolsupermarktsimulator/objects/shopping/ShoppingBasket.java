package com.example.schoolsupermarktsimulator.objects.shopping;

/**
 * Kleine winkelmand. De opslaglogica komt van {@link MoveableInventory};
 * een mand verschilt van een wagen alleen door dit lagere maximum.
 */
public class ShoppingBasket extends MoveableInventory {

	/** Een mand raakt vol na vijf artikelen. */
	public static final int MAX_CAPACITY = 5;

	public ShoppingBasket() {
		super(MAX_CAPACITY);
	}
}
