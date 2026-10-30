package com.example.schoolsupermarktsimulator.objects.shopping;

/**
 * Vulkar van een vakkenvuller. Die brengt voorraad van het magazijn naar een winkelschap.
 */
public class StockTrolley extends MoveableInventory {

	/** Een vulkar raakt vol na twaalf artikelen. */
	public static final int MAX_CAPACITY = 12;

	public StockTrolley() {
		super(MAX_CAPACITY);
	}
}
