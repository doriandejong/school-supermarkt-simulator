package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Een schap met een maximum en een minimumvoorraad.
 * Zakt de voorraad tot het minimum, dan vraagt het schap het magazijn om bij te vullen.
 * Die aanvraag loopt via {@link Warehouse} naar de WarehouseController.
 */
public abstract class Shelf extends GameObject {

	private final String shelfId;
	private Node shelfNode;
	private final int capacity;
	private final int minimumInventory;
	private final List<Product> products;
	private final Warehouse warehouse;
	private Product prototype;
	private Tooltip tooltip;
	private Runnable onStockChanged;

	/**
	 * @param x horizontale plek van het schap
	 * @param y verticale plek van het schap
	 * @param shelfId herkenbare code, bijvoorbeeld REGULAR-PASTA
	 * @param capacity hoeveel artikelen er maximaal op passen
	 * @param minimumInventory voorraad waarbij een aanvulling wordt aangevraagd
	 * @param warehouse magazijn dat de aanvulling ontvangt
	 */
	public Shelf(double x, double y, String shelfId, int capacity, int minimumInventory, Warehouse warehouse) {
		super(x, y);
		this.shelfId = shelfId;
		this.capacity = capacity;
		this.minimumInventory = minimumInventory;
		this.warehouse = warehouse;
		this.products = new ArrayList<>();
	}

	/** @return de code van dit schap */
	public String getShelfId() {
		return shelfId;
	}

	/** @return de loopknoop waar een persoon voor dit schap stopt */
	public Node getShelfNode() {
		return shelfNode;
	}

	/**
	 * Koppel de knoop waar een klant of vakkenvuller naartoe loopt.
	 *
	 * @param shelfNode knoop uit de nodemap, vlak bij dit schap
	 */
	public void setShelfNode(Node shelfNode) {
		this.shelfNode = shelfNode;
	}

	/** @return voorraad waarbij {@link Warehouse#requestRestock(Shelf)} wordt aangeroepen */
	public int getMinimumInventory() {
		return minimumInventory;
	}

	/**
	 * Callback voor de plattegrond, zodat de getekende voorraad meebeweegt met de aantallen.
	 *
	 * @param onStockChanged wordt aangeroepen na pakken of vullen
	 */
	public void setOnStockChanged(Runnable onStockChanged) {
		this.onStockChanged = onStockChanged;
	}

	/**
	 * Maakt een nieuw exemplaar van het artikel dat dit schap verkoopt.
	 * Een magazijnschap verkoopt zelf niets en geeft null terug.
	 *
	 * @return een nieuw artikel, of null als dit schap geen vast artikel heeft
	 */
	public Product createStockItem() {
		if (prototype == null) {
			return null;
		}
		return new Product(prototype.getId(), prototype.getName(), prototype.getPrice());
	}

	/**
	 * Mag dit artikel op dit schap? Een winkelschap accepteert alleen zijn eigen artikel.
	 * Een schap zonder vast artikel, zoals een magazijnschap, accepteert alles.
	 *
	 * @param product artikel dat iemand wil neerzetten
	 * @return true als het schap dit artikel aanneemt
	 */
	public boolean accepts(Product product) {
		if (product == null || product.getName() == null) {
			return false;
		}
		Product sample = createStockItem();
		if (sample == null || sample.getName() == null) {
			return true;
		}
		return sample.getName().equals(product.getName());
	}

	/**
	 * Onthoudt het eerste artikel als het artikel dat dit schap verkoopt.
	 * Subklassen met een vast soort hoeven dit niet te gebruiken.
	 *
	 * @param product eerste artikel dat op het schap is gezet
	 */
	protected void rememberStockedProduct(Product product) {
		if (prototype == null && product != null) {
			prototype = product;
		}
	}

	/** Koppelt de tooltip aan de sprite. Aanroepen zodra de sprite in de subklasse bestaat. */
	protected void setupTooltip() {
		if (sprite != null) {
			tooltip = new Tooltip();
			tooltip.setShowDelay(Duration.millis(100));
			Tooltip.install(sprite, tooltip);
			updateTooltipText();
		}
	}

	/** Zet de tooltip op het huidige aantal tegen de capaciteit, bijvoorbeeld 10/20. */
	private void updateTooltipText() {
		if (tooltip != null) {
			tooltip.setText(products.size() + "/" + capacity);
		}
	}

	/** Werkt tooltip bij en vertelt de plattegrond dat de voorraad veranderd is. */
	private void notifyStockChanged() {
		updateTooltipText();
		if (onStockChanged != null) {
			onStockChanged.run();
		}
	}

	/**
	 * Zet artikelen op het schap tot de capaciteit vol is.
	 *
	 * @param newProducts artikelen die erbij moeten, in volgorde
	 * @return hoeveel artikelen er echt bij zijn gekomen
	 */
	public int fillShelf(List<Product> newProducts) {
		if (newProducts == null) {
			return 0;
		}
		int addedCount = 0;
		for (Product product : newProducts) {
			if (products.size() >= capacity) {
				break;
			}
			products.add(product);
			rememberStockedProduct(product);
			addedCount++;
		}
		notifyStockChanged();
		return addedCount;
	}

	/**
	 * Haalt één exemplaar met dezelfde naam van het schap.
	 * Raakt de voorraad precies het minimum, dan gaat de aanvulling naar het magazijn.
	 *
	 * @param product artikel waarvan de naam gezocht wordt
	 * @return het exemplaar dat op het schap lag, of null als die naam er niet was
	 */
	public Product takeProduct(Product product) {
		if (product == null || product.getName() == null || products.isEmpty()) {
			return null;
		}

		Product toRemove = products.stream()
				.filter(p -> product.getName().equals(p.getName()))
				.findFirst()
				.orElse(null);
		if (toRemove == null || !products.remove(toRemove)) {
			return null;
		}

		// Alleen op het moment dat de drempel geraakt wordt, niet als de voorraad er al onder zit.
		if (products.size() == minimumInventory && warehouse != null) {
			warehouse.requestRestock(this);
		}
		notifyStockChanged();
		return toRemove;
	}

	/** @return de artikelen die nu op het schap liggen */
	public List<Product> getProducts() {
		return this.products;
	}

	/**
	 * @param product artikel waarvan alleen de naam telt
	 * @return true als er minstens één exemplaar met die naam ligt
	 */
	public boolean hasProduct(Product product) {
		if (product == null || product.getName() == null) {
			return false;
		}
		return this.products.stream().anyMatch(p -> product.getName().equals(p.getName()));
	}

	/** @return hoeveel artikelen er nu op liggen */
	public int getCurrentStockCount() {
		return products.size();
	}

	/** @return hoeveel artikelen er maximaal op passen */
	public int getCapacity() {
		return capacity;
	}
}
