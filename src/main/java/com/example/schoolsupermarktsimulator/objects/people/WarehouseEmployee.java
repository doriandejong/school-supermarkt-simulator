package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import javafx.scene.paint.Color;

/**
 * Magazijnmedewerker. Scant de artikelen uit een doos voordat die op een magazijnschap gaan.
 */
public class WarehouseEmployee extends Employee {

	/**
	 * @param startNode knoop bij de vrachtwagen of de magazijnschappen
	 * @param id personeelsnummer
	 * @param name naam van de magazijnmedewerker
	 */
	public WarehouseEmployee(Node startNode, int id, String name) {
		super(startNode, id, name);
		sprite.setFill(Color.MEDIUMPURPLE);
	}

	/** @return Magazijnmedewerker, zichtbaar als je over het figuur hovert */
	@Override
	public String roleName() {
		return "Magazijnmedewerker";
	}

	/**
	 * Scant één artikel uit een aangekomen doos.
	 *
	 * @param product artikel uit de doos van de vrachtwagen
	 * @return hetzelfde artikel, of null als er niets te scannen was
	 */
	public Product scanProduct(Product product) {
		return scan(product);
	}
}
