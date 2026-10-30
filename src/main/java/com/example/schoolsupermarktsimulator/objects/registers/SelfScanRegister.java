package com.example.schoolsupermarktsimulator.objects.registers;

import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.people.Employee;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

/**
 * Zelfscan. De klant scant zelf en betaalt zonder kassamedewerker,
 * tenzij een steekproef afgaat. Dan moet een medewerker één artikel controleren.
 */
public class SelfScanRegister extends Register {

	private static final Rectangle2D REGISTER_VIEWPORT = new Rectangle2D(116, 74, 792, 489);

	/** Kans dat een betaling een steekproef krijgt. 0 is nooit, 1 is altijd. */
	private double inspectionChance = 0.25;

	/** Uitkomst die de volgende controle moet gebruiken, als die al vooraf is bepaald. */
	private Boolean heldInspection;

	public SelfScanRegister(double x, double y) {
		super(x, y);

		Image image = new Image(
				Objects.requireNonNull(SelfScanRegister.class.getResourceAsStream(
						"/assets/objects/selfscan.png"
				))
		);

		sprite = new ImageView(image);
		sprite.setViewport(REGISTER_VIEWPORT);
		sprite.setX(x);
		sprite.setY(y);
		sprite.setFitWidth(160);
		sprite.setPreserveRatio(true);
	}

	/**
	 * Zet de kans op een steekproef. Tests gebruiken 0 of 1 zodat de uitkomst vastligt.
	 *
	 * @param chance kans tussen 0 en 1
	 */
	public void setInspectionChance(double chance) {
		if (chance < 0) {
			chance = 0;
		} else if (chance > 1) {
			chance = 1;
		}
		this.inspectionChance = chance;
	}

	/**
	 * Gooit de dobbelsteen voor een steekproef, zonder die uitkomst meteen te verbruiken.
	 * {@link #holdInspection(boolean)} zet daarna vast wat de betaling doet.
	 *
	 * @return true als een medewerker één artikel moet nakijken
	 */
	public boolean rollInspection() {
		return Math.random() < inspectionChance;
	}

	/**
	 * Zet vast of de volgende betaling een steekproef is.
	 * De eerstvolgende {@link #doRandomInspection()} gebruikt deze uitkomst één keer.
	 *
	 * @param inspect true als de manager moet controleren
	 */
	public void holdInspection(boolean inspect) {
		this.heldInspection = inspect;
	}

	/**
	 * Bepaalt of deze betaling gecontroleerd wordt.
	 * Een eerder vastgezette uitkomst gaat voor op een nieuwe worp.
	 *
	 * @return true als een medewerker één artikel moet nakijken
	 */
	public boolean doRandomInspection() {
		if (heldInspection != null) {
			boolean inspect = heldInspection;
			heldInspection = null;
			return inspect;
		}
		return Math.random() < inspectionChance;
	}

	/**
	 * De klant betaalt zelf. Gaat de steekproef af, dan scant de meegegeven medewerker
	 * het eerste artikel. Zonder medewerker blijft de betaling dan staan.
	 *
	 * @param customer klant die zelf scant
	 * @param inspector kassamedewerker voor de steekproef, mag null zijn als er geen controle is
	 * @return true als de betaling gelukt is
	 */
	@Override
	public boolean completeCheckout(Customer customer, Employee inspector) {
		if (customer == null || !isOpen()) {
			return false;
		}
		if (!proceedToRegister(customer)) {
			return false;
		}
		if (doRandomInspection()) {
			if (inspector == null) {
				return false;
			}
			List<Product> products = productsOf(customer);
			if (!products.isEmpty()) {
				inspector.scan(products.getFirst());
			}
		}
		boolean paid = customer.payGroceries();
		finishCurrentCustomer();
		return paid;
	}

}
