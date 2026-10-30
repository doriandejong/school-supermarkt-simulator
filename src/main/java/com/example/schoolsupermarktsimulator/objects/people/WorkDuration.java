package com.example.schoolsupermarktsimulator.objects.people;

/**
 * Hoe lang een handeling duurt voordat iemand weer verder loopt.
 * Pakken, vullen en afrekenen kosten tijd; de persoon blijft intussen staan.
 */
public final class WorkDuration {

	/** Seconden dat een klant bij één schap staat om een artikel te pakken. */
	public static final double TAKE_FROM_SHELF_SECONDS = 1.5;

	/** Seconden per artikel dat een vakkenvuller op een schap zet. */
	public static final double PLACE_PER_PRODUCT_SECONDS = 0.4;

	/** Minimale tijd om een schap bij te vullen, ook bij één artikel. */
	public static final double PLACE_MINIMUM_SECONDS = 1.5;

	/** Seconden dat de magazijnmedewerker een doos op het schap zet. */
	public static final double PLACE_BOX_SECONDS = 2.0;

	/** Vaste tijd aan de kassa, bovenop de tijd per artikel. */
	public static final double CHECKOUT_BASE_SECONDS = 2.0;

	/** Extra seconden per artikel dat gescand en betaald wordt. */
	public static final double CHECKOUT_PER_ITEM_SECONDS = 0.5;

	/** Seconden dat de manager bij iemand blijft staan die om hulp vroeg. */
	public static final double HELP_SECONDS = 2.0;

	private WorkDuration() {
	}

	/**
	 * Tijd om een lading op een schap te zetten.
	 *
	 * @param count aantal artikelen dat op het schap gaat
	 * @return seconden, minstens {@link #PLACE_MINIMUM_SECONDS}
	 */
	public static double placeProducts(int count) {
		return Math.max(PLACE_MINIMUM_SECONDS, Math.max(0, count) * PLACE_PER_PRODUCT_SECONDS);
	}

	/**
	 * Tijd om af te rekenen.
	 *
	 * @param items aantal artikelen in de mand of wagen
	 * @return seconden aan de kassa
	 */
	public static double checkout(int items) {
		return CHECKOUT_BASE_SECONDS + Math.max(0, items) * CHECKOUT_PER_ITEM_SECONDS;
	}
}
