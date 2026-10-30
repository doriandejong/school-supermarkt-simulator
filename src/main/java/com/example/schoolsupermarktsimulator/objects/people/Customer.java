package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shopping.MoveableInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Een klant met een budget, een boodschappenlijst en een mand of wagen.
 * De mand en de wagen zijn allebei een {@link MoveableInventory}; de klant merkt alleen of er nog ruimte is.
 */
public class Customer extends Person {

	private double budget = 30;
	private double spent;
	private MoveableInventory aid;
	private List<Product> groceryList = new ArrayList<>();

	/**
	 * @param startNode knoop waar de klant de winkel binnenkomt
	 * @param id nummer van deze klant
	 * @param name naam van deze klant
	 */
	public Customer(Node startNode, int id, String name) {
		super(startNode, id, name);
	}

	/**
	 * @param startNode knoop waar de klant de winkel binnenkomt
	 * @param id nummer van deze klant
	 * @param name naam van deze klant
	 * @param speed loopsnelheid in pixels per seconde
	 */
	public Customer(Node startNode, int id, String name, double speed) {
		super(startNode, id, name, speed);
	}

	/** @return Klant, zichtbaar als je over het figuur hovert */
	@Override
	public String roleName() {
		return "Klant";
	}

	/** @return het budget in euro dat deze klant mag uitgeven */
	public double getBudget() {
		return budget;
	}

	/**
	 * Zet hoeveel euro de klant bij zich heeft.
	 *
	 * @param budget budget in euro, negatief wordt 0
	 */
	public void setBudget(double budget) {
		this.budget = Math.max(0, budget);
	}

	/** @return hoeveel euro er al in de mand of wagen zit */
	public double getSpent() {
		return spent;
	}

	/** @return de mand of wagen, of null als de klant nog niets bij zich heeft */
	public MoveableInventory getAid() {
		return aid;
	}

	/**
	 * Geeft de klant een mand of een wagen. Een volle mand weigert daarna extra artikelen.
	 *
	 * @param aid de mand, wagen of andere voorraad die de klant draagt
	 */
	public void attachAid(MoveableInventory aid) {
		this.aid = aid;
	}

	/** @return de artikelen die de klant wil kopen */
	public List<Product> getGroceryList() {
		return groceryList;
	}

	/**
	 * Vervangt de boodschappenlijst. Null wordt een lege lijst, zodat het zoeken niet stukloopt.
	 *
	 * @param groceryList gewenste artikelen, vergeleken op naam
	 */
	public void setGroceryList(List<Product> groceryList) {
		this.groceryList = groceryList == null ? new ArrayList<>() : groceryList;
	}

	/**
	 * Past dit artikel nog binnen het budget dat nog over is?
	 *
	 * @param product artikel met een prijs
	 * @return true als spent + prijs niet boven het budget uitkomt
	 */
	public boolean canAfford(Product product) {
		if (product == null) {
			return false;
		}
		return spent + product.getPrice() <= budget;
	}

	/**
	 * Stopt een artikel in de mand of wagen als er ruimte is en het budget het toelaat.
	 *
	 * @param product het exemplaar dat van het schap komt
	 * @return true als het artikel echt in de mand of wagen ligt
	 */
	public boolean getProduct(Product product) {
		if (product == null || aid == null || !canAfford(product)) {
			return false;
		}
		if (!aid.addProduct(product)) {
			return false;
		}
		spent += product.getPrice();
		return true;
	}

	/**
	 * Haalt een artikel uit de mand of wagen en trekt de prijs van het uitgegeven bedrag af.
	 *
	 * @param product het exemplaar dat terug moet
	 * @return true als het exemplaar in de mand of wagen zat
	 */
	public boolean putBackProduct(Product product) {
		if (aid == null || product == null || !aid.removeProduct(product)) {
			return false;
		}
		spent -= product.getPrice();
		if (spent < 0) {
			spent = 0;
		}
		return true;
	}

	/**
	 * Pakt van dit schap één artikel dat op de boodschappenlijst staat.
	 * Lukt het niet om het in de mand te leggen, dan gaat het meteen terug het schap in.
	 *
	 * @param shelf het schap waar de klant voor staat
	 * @return het meegenomen exemplaar, of null als er niets is meegenomen
	 */
	public Product takeFromShelf(Shelf shelf) {
		if (shelf == null) {
			return null;
		}
		Product wanted = findWantedProduct(shelf);
		if (wanted == null) {
			return null;
		}

		Product taken = shelf.takeProduct(wanted);
		if (taken == null) {
			return null;
		}
		// Geen ruimte of geen budget: het artikel hoort weer op het schap, niet in de mand.
		if (!getProduct(taken)) {
			shelf.fillShelf(List.of(taken));
			return null;
		}
		return taken;
	}

	/**
	 * @param shelf schap waar de klant voor staat
	 * @return true als er op dit schap iets van de boodschappenlijst ligt
	 */
	public boolean wantsFrom(Shelf shelf) {
		return shelf != null && findWantedProduct(shelf) != null;
	}

	/**
	 * Zoekt op het schap het eerste artikel waarvan de naam op de boodschappenlijst staat.
	 *
	 * @param shelf schap dat de klant bekijkt
	 * @return een gewenst artikel op dat schap, of null
	 */
	private Product findWantedProduct(Shelf shelf) {
		for (Product want : groceryList) {
			if (want != null && shelf.hasProduct(want)) {
				return want;
			}
		}
		return null;
	}

	/**
	 * Betaalt de inhoud van de mand of wagen als het uitgegeven bedrag binnen het budget valt.
	 * Na een geslaagde betaling is de mand leeg en staat het uitgegeven bedrag weer op 0.
	 *
	 * @return true als er betaald is
	 */
	public boolean payGroceries() {
		if (spent > budget) {
			return false;
		}
		if (aid != null) {
			aid.clear();
		}
		spent = 0;
		return true;
	}
}
