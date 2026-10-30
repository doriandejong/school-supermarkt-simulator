package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shopping.StockTrolley;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vakkenvuller. Haalt artikelen van de vulkar en zet ze op een winkelschap,
 * maar alleen als het schap dat artikel verkoopt en er nog plek is.
 */
public class ShelfStocker extends Employee {

	private StockTrolley trolley;
	private ImageView carriedBox;

	/**
	 * @param startNode knoop waar de vakkenvuller begint, meestal in het magazijn
	 * @param id personeelsnummer
	 * @param name naam van de vakkenvuller
	 */
	public ShelfStocker(Node startNode, int id, String name) {
		super(startNode, id, name);
		sprite.setFill(Color.DARKORANGE);
	}

	/** @return Vakkenvuller, zichtbaar als je over het figuur hovert */
	@Override
	public String roleName() {
		return "Vakkenvuller";
	}

	/** @return de vulkar, of null als er nog geen kar is gekoppeld */
	public StockTrolley getTrolley() {
		return trolley;
	}

	/**
	 * Koppel de kar waar de voorraad op ligt tijdens het lopen naar een schap.
	 *
	 * @param trolley kar met een eigen maximum
	 */
	public void attachTrolley(StockTrolley trolley) {
		this.trolley = trolley;
	}

	/**
	 * Laat een doos in de handen zien. Elke vakkenvuller heeft een eigen doos,
	 * zodat twee vulbeurten niet dezelfde tekening delen.
	 */
	public void showCarriedBox() {
		if (carriedBox == null) {
			Image image = new Image(Objects.requireNonNull(
					ShelfStocker.class.getResourceAsStream("/assets/objects/box_brown.png")
			));
			carriedBox = new ImageView(image);
			carriedBox.setFitWidth(28);
			carriedBox.setPreserveRatio(true);
			carry(carriedBox, -6, -32);
		}
		carriedBox.setVisible(true);
		carriedBox.toFront();
	}

	/** Haalt de doos uit de handen, op het moment dat het winkelschap gevuld wordt. */
	public void hideCarriedBox() {
		if (carriedBox != null) {
			carriedBox.setVisible(false);
		}
	}

	/**
	 * Zet van de kar alle artikelen op het schap die dat schap accepteert, tot het schap vol is.
	 *
	 * @param shelf het winkelschap dat bijgevuld wordt
	 * @return hoeveel artikelen er echt op het schap zijn gezet
	 */
	public int fillShelf(Shelf shelf) {
		if (shelf == null || trolley == null) {
			return 0;
		}
		List<Product> moving = new ArrayList<>();
		for (Product product : trolley.getProducts()) {
			if (shelf.getCurrentStockCount() + moving.size() >= shelf.getCapacity()) {
				break;
			}
			if (shelf.accepts(product)) {
				moving.add(product);
			}
		}
		for (Product product : moving) {
			trolley.removeProduct(product);
		}
		return shelf.fillShelf(moving);
	}
}
