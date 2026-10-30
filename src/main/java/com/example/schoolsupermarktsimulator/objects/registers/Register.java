package com.example.schoolsupermarktsimulator.objects.registers;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Employee;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.products.Product;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Een kassa waar een klant kan afrekenen.
 * Is de kassa bezet, dan sluit de volgende klant achteraan in de rij.
 * De gewone kassa en de zelfscan vullen {@link #completeCheckout(Customer, Employee)} zelf in.
 */
public abstract class Register extends GameObject {

	private static int nextId = 1;

	private final int registerId;
	private boolean open = true;
	private final Queue<Customer> queue = new ArrayDeque<>();
	private final List<Node> queueSpots = new ArrayList<>();
	private Customer current;
	private Node queueNode;

	/**
	 * @param x horizontale plek van de kassa
	 * @param y verticale plek van de kassa
	 */
	public Register(double x, double y) {
		super(x, y);
		this.registerId = nextId++;
	}

	/** @return het nummer van deze kassa */
	public int getRegisterId() {
		return registerId;
	}

	/** @return true als er klanten door mogen */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Open of sluit de kassa. Een gesloten kassa neemt geen nieuwe klant aan.
	 *
	 * @param open true om klanten toe te laten
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/** @return true als de kassa open is en er niemand aan het afrekenen is */
	public boolean isFree() {
		return open && current == null;
	}

	/** @return de klant die nu afrekent, of null */
	public Customer getCurrentCustomer() {
		return current;
	}

	/** @return de knoop waar een klant in de rij gaat staan */
	public Node getQueueNode() {
		return queueNode;
	}

	/**
	 * Zet de knoop waar een klant naartoe loopt om af te rekenen.
	 *
	 * @param queueNode knoop vlak voor deze kassa
	 */
	public void setQueueNode(Node queueNode) {
		this.queueNode = queueNode;
	}

	/**
	 * Zet de plekken van de wachtrij, vooraan bij de kassa tot achteraan.
	 * De eerste knoop is waar de klant afrekent. De volgende zijn wachtplekken daarachter.
	 *
	 * @param spotsFrontToBack rijplekken, mag leeg zijn
	 */
	public void setQueueSpots(List<Node> spotsFrontToBack) {
		queueSpots.clear();
		if (spotsFrontToBack == null) {
			return;
		}
		for (Node spot : spotsFrontToBack) {
			if (spot != null && !queueSpots.contains(spot)) {
				queueSpots.add(spot);
			}
		}
		if (!queueSpots.isEmpty()) {
			queueNode = queueSpots.getFirst();
		}
	}

	/**
	 * Zet deze klant in de rij. Is de kassa vrij, dan mag die meteen vooraan staan.
	 *
	 * @param customer klant die wil afrekenen
	 * @return de knoop waar deze klant moet gaan staan, of null als dat niet kan
	 */
	public Node joinQueue(Customer customer) {
		if (customer == null || !open) {
			return null;
		}
		if (current != customer && !queue.contains(customer)) {
			if (current == null) {
				current = customer;
			} else {
				queue.add(customer);
			}
		}
		return spotForIndex(indexInLine(customer));
	}

	/**
	 * @return de klant die afrekent, gevolgd door wie erachter wacht
	 */
	public List<Customer> waitingLine() {
		List<Customer> line = new ArrayList<>();
		if (current != null) {
			line.add(current);
		}
		line.addAll(queue);
		return line;
	}

	/**
	 * Plek in de rij. 0 is vooraan bij de kassa. Staat de rij vol, dan deelt de laatste plek.
	 *
	 * @param index 0 voor de klant die afrekent
	 * @return de knoop voor die plek
	 */
	public Node spotForIndex(int index) {
		if (queueSpots.isEmpty()) {
			return queueNode;
		}
		if (index < 0) {
			index = 0;
		}
		if (index >= queueSpots.size()) {
			return queueSpots.getLast();
		}
		return queueSpots.get(index);
	}

	/** @return hoe ver deze klant van de kassa af staat, 0 is vooraan */
	private int indexInLine(Customer customer) {
		if (customer == current) {
			return 0;
		}
		int index = 1;
		for (Customer waiting : queue) {
			if (waiting == customer) {
				return index;
			}
			index++;
		}
		return 0;
	}

	/**
	 * Laat de klant aan de beurt komen, of zet de klant in de rij als de kassa bezet is.
	 *
	 * @param customer klant die wil afrekenen
	 * @return true als de klant meteen aan de beurt is
	 */
	public boolean proceedToRegister(Customer customer) {
		if (customer == null || !open) {
			return false;
		}
		if (current == customer) {
			return true;
		}
		if (isFree()) {
			current = customer;
			return true;
		}
		if (!queue.contains(customer)) {
			queue.add(customer);
		}
		return false;
	}

	/**
	 * Telt de prijzen van alles wat de klant in de mand of wagen heeft.
	 *
	 * @param customer klant die afrekent
	 * @return het totaalbedrag in euro
	 */
	public double calculateTotal(Customer customer) {
		double total = 0;
		for (Product product : productsOf(customer)) {
			total += product.getPrice();
		}
		return total;
	}

	/**
	 * Rekent deze klant af. Een gewone kassa gebruikt zijn eigen kassamedewerker;
	 * een zelfscan gebruikt de meegegeven medewerker alleen bij een steekproef.
	 *
	 * @param customer klant die aan de beurt hoort te zijn
	 * @param inspector medewerker voor een steekproef, mag null zijn bij een gewone kassa
	 * @return true als de betaling gelukt is
	 */
	public abstract boolean completeCheckout(Customer customer, Employee inspector);

	/**
	 * @param customer klant met een mand of wagen
	 * @return de artikelen die afgerekend moeten worden
	 */
	protected List<Product> productsOf(Customer customer) {
		if (customer == null || customer.getAid() == null) {
			return List.of();
		}
		return customer.getAid().getProducts();
	}

	/**
	 * De huidige klant is klaar. De volgende uit de rij mag naar voren, maar rekent nog niet zelf af.
	 */
	protected void finishCurrentCustomer() {
		current = null;
		if (!queue.isEmpty()) {
			current = queue.remove();
		}
	}

	/** @return hoeveel klanten er nog wachten, de klant die nu afrekent niet meegerekend */
	public int getQueueLength() {
		return queue.size();
	}
}
