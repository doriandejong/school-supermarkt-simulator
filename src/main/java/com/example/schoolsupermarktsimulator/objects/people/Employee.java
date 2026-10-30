package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Iemand die in de winkel werkt. Kassamedewerker, vakkenvuller, magazijnmedewerker en manager erven hiervan.
 */
public class Employee extends Person {

	private final List<Product> scanned = new ArrayList<>();

	/**
	 * @param startNode knoop waar de medewerker begint
	 * @param id nummer, dit is ook het personeelsnummer
	 * @param name naam van de medewerker
	 */
	public Employee(Node startNode, int id, String name) {
		super(startNode, id, name);
		sprite.setFill(Color.DODGERBLUE);
	}

	/** @return Medewerker, tenzij een specifiekere functie dat overschrijft */
	@Override
	public String roleName() {
		return "Medewerker";
	}

	/**
	 * Scant één artikel. De prijs blijft op het artikel staan.
	 *
	 * @param product het artikel dat langs de scanner gaat
	 * @return hetzelfde artikel, of null als er niets te scannen was
	 */
	public Product scan(Product product) {
		if (product == null) {
			return null;
		}
		scanned.add(product);
		return product;
	}

	/** @return de artikelen die deze medewerker gescand heeft, in volgorde */
	public List<Product> getScannedProducts() {
		return List.copyOf(scanned);
	}
}
