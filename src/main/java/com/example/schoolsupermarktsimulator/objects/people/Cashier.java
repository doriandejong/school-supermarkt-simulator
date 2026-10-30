package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import javafx.scene.paint.Color;

/**
 * Kassamedewerker aan een gewone kassa.
 * Scant artikelen bij het afrekenen en kan bij de zelfscan een steekproef doen.
 */
public class Cashier extends Employee {

	/**
	 * @param startNode knoop bij de kassa
	 * @param id personeelsnummer
	 * @param name naam van de kassamedewerker
	 */
	public Cashier(Node startNode, int id, String name) {
		super(startNode, id, name);
		sprite.setFill(Color.ROYALBLUE);
	}

	/** @return Kassamedewerker, zichtbaar als je over het figuur hovert */
	@Override
	public String roleName() {
		return "Kassamedewerker";
	}
}
