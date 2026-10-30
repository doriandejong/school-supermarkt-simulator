package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.ShelfStocker;
import com.example.schoolsupermarktsimulator.objects.people.WarehouseEmployee;
import com.example.schoolsupermarktsimulator.objects.people.WorkDuration;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.warehouse.BoxStack;
import com.example.schoolsupermarktsimulator.objects.warehouse.Truck;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Regelt het magazijn: dozen van de vrachtwagen naar de magazijnschappen,
 * en van daaruit naar een winkelschap dat op zijn minimum zit.
 * {@link Warehouse} bewaart de schappen en stuurt elke aanvraag hiernaartoe.
 */
public class WarehouseController {

	/** Artikelen in één doos die de vrachtwagen neerzet en die op een magazijnschap gaat. */
	static final int ITEMS_PER_BOX = 2;

	private Warehouse warehouse;
	private Truck truck;
	private BoxStack boxStack;
	private final List<WarehouseEmployee> employees = new ArrayList<>();
	private final List<ShelfStocker> stockers = new ArrayList<>();
	private final Map<String, Integer> reservedStock = new HashMap<>();
	private final Queue<BoxDelivery> pendingBoxes = new ArrayDeque<>();
	private Runnable deliveryDone;
	private Node dockNode;
	private final List<Shelf> shopShelves = new ArrayList<>();
	private final List<Shelf> pending = new ArrayList<>();
	private boolean transporting;
	private boolean deliveryWaiting;
	private boolean pumping;
	private boolean restockQueued;

	/**
	 * Koppel het magazijn, de vrachtwagen en de stapel dozen die de vrachtwagen achterlaat.
	 *
	 * @param warehouse de voorraadruimte
	 * @param truck de vrachtwagen die levert, mag null zijn in een test
	 * @param boxStack de zichtbare dozen bij de laadklep, mag null zijn
	 */
	public void bind(Warehouse warehouse, Truck truck, BoxStack boxStack) {
		this.warehouse = warehouse;
		this.truck = truck;
		this.boxStack = boxStack;
		if (warehouse != null) {
			warehouse.setController(this);
			if (truck != null) {
				warehouse.setDeliveryTruck(truck);
			}
		}
	}

	/**
	 * Onthoud een winkelschap, zodat een vrachtwagen weet welk artikel bijgevuld moet worden.
	 *
	 * @param shelf schap in de winkel, geen magazijnschap
	 */
	public void registerShopShelf(Shelf shelf) {
		if (shelf == null || shelf instanceof WarehouseShelf || shopShelves.contains(shelf)) {
			return;
		}
		shopShelves.add(shelf);
	}

	/**
	 * Voegt een magazijnmedewerker toe die dozen van de vrachtwagen naar een magazijnschap brengt.
	 * Meerdere medewerkers pakken ieder een eigen doos.
	 *
	 * @param employee de medewerker, mag null zijn
	 */
	public void setWarehouseEmployee(WarehouseEmployee employee) {
		if (employee != null && !employees.contains(employee)) {
			employees.add(employee);
		}
	}

	/**
	 * Voegt een vakkenvuller toe die voorraad van het magazijn naar de winkel brengt.
	 * Meerdere vakkenvullers vullen tegelijk verschillende schappen.
	 *
	 * @param stocker de vakkenvuller, mag null zijn
	 */
	public void setShelfStocker(ShelfStocker stocker) {
		if (stocker != null && !stockers.contains(stocker)) {
			stockers.add(stocker);
		}
	}

	/**
	 * @param dockNode de knoop bij de dozen, waar de magazijnmedewerker een doos oppakt
	 */
	public void setDockNode(Node dockNode) {
		this.dockNode = dockNode;
	}

	/**
	 * Een schap zit op zijn minimum.
	 * Een winkelschap wordt vanuit het magazijn bijgevuld. Lukt dat niet, dan komt er een vrachtwagen.
	 * Een leeg magazijnschap bestelt alleen een vrachtwagen.
	 *
	 * @param shelf het schap dat de drempel heeft geraakt
	 */
	public void requestRestock(Shelf shelf) {
		if (shelf == null || warehouse == null) {
			return;
		}
		if (shelf instanceof WarehouseShelf) {
			System.out.println("Magazijnschap is leeg. Er wordt een vrachtwagen besteld.");
			orderTruck();
			return;
		}
		if (!pending.contains(shelf)) {
			pending.add(shelf);
		}
		System.out.println("Drempelwaarde bereikt voor " + shelf.getShelfId() + ". Voorraad komt uit het magazijn.");
		if (hasStockers() && idleStocker() == null && hasStockFor(shelf)) {
			restockQueued = true;
			return;
		}
		int moved = supplyShopShelf(shelf);
		if (moved > 0 || !needsRestock(shelf)) {
			if (!needsRestock(shelf)) {
				pending.remove(shelf);
			}
			return;
		}
		orderTruck();
	}

	/**
	 * Bestelt de vrachtwagen als er nog plek is op een magazijnschap.
	 * Is de vrachtwagen al onderweg, dan plant die zelf een tweede rit.
	 */
	public void orderTruck() {
		if (truck == null || !warehouseHasRoom()) {
			return;
		}
		truck.deliver(this::warehouseHasRoom, () -> warehouse.recieveTruck(truck));
	}

	/**
	 * De vrachtwagen is aangekomen. Elke doos gaat naar een magazijnschap,
	 * meegedragen door de magazijnmedewerker als die een route heeft.
	 * Daarna vult de vakkenvuller de winkelschappen die om voorraad vroegen.
	 *
	 * @param arrived de aangekomen vrachtwagen, mag null zijn als de dozen al gepland zijn
	 */
	public void receiveTruck(Truck arrived) {
		if (warehouse == null) {
			return;
		}
		if (transporting) {
			deliveryWaiting = true;
			return;
		}
		transporting = true;
		System.out.println("Vrachtwagen is aangekomen. Dozen gaan naar de magazijnschappen.");
		List<BoxDelivery> boxes = planBoxes();
		if (boxStack != null) {
			boxStack.show(boxes.size());
		}
		transportBoxes(new ArrayDeque<>(boxes), () -> {
			transporting = false;
			fulfillPendingShelves();
			fillFromAvailableStock();
			if (deliveryWaiting) {
				deliveryWaiting = false;
				receiveTruck(arrived);
			}
		});
	}

	/**
	 * Haalt artikelen met het gevraagde id of dezelfde naam uit de magazijnschappen.
	 *
	 * @param productId id of naam
	 * @return de gevonden exemplaren
	 */
	public List<Product> getStock(String productId) {
		if (productId == null || warehouse == null) {
			return List.of();
		}
		List<Product> found = new ArrayList<>();
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			for (Product product : shelf.getProducts()) {
				if (productId.equals(product.getId()) || productId.equals(product.getName())) {
					found.add(product);
				}
			}
		}
		for (Product product : warehouse.getLooseStock()) {
			if (productId.equals(product.getId()) || productId.equals(product.getName())) {
				found.add(product);
			}
		}
		return found;
	}

	/**
	 * Zet voorraad van de magazijnschappen op één winkelschap.
	 * De vakkenvuller loopt erheen als er een knoop en een kar zijn; anders gaat de voorraad meteen op het schap.
	 *
	 * @param shopShelf schap in de winkel
	 * @return hoeveel artikelen er op de kar of op het schap zijn gezet
	 */
	public int supplyShopShelf(Shelf shopShelf) {
		if (shopShelf == null || shopShelf instanceof WarehouseShelf) {
			return 0;
		}
		ShelfStocker worker = idleStocker();
		if (hasStockers() && worker == null) {
			restockQueued = true;
			return 0;
		}
		int missing = shopShelf.getCapacity() - shopShelf.getCurrentStockCount();
		if (missing <= 0) {
			return 0;
		}
		int available = availableFor(shopShelf);
		boolean canWalk = worker != null
				&& worker.getTrolley() != null
				&& shopShelf.getShelfNode() != null;
		if (!canWalk) {
			List<Product> taken = takeMatching(shopShelf, Math.min(missing, available));
			return taken.isEmpty() ? 0 : shopShelf.fillShelf(taken);
		}
		int planned = fillAmount(shopShelf);
		if (planned <= 0 || available < planned || worker.getTrolley().remainingSpace() < planned) {
			return 0;
		}
		Product sample = shopShelf.createStockItem();
		String stockName = sample == null ? null : sample.getName();
		reserve(stockName, planned);
		Node pickup = sourceShelf(shopShelf);
		System.out.println(worker.getRoleLabel() + " haalt " + planned + " artikelen voor " + shopShelf.getShelfId() + ".");
		worker.setOnArrived(node -> {
			if (pickup == null || pickup.equals(node)) {
				worker.showCarriedBox();
			}
		});
		worker.setOnRouteFinished(() -> worker.dwell(WorkDuration.placeProducts(planned), () -> {
			// Dozen blijven op het magazijnschap tot het winkelschap echt gevuld wordt.
			worker.hideCarriedBox();
			List<Product> taken = takeMatching(shopShelf, planned);
			release(stockName, planned);
			for (Product product : taken) {
				worker.getTrolley().addProduct(product);
			}
			worker.fillShelf(shopShelf);
			if (!needsRestock(shopShelf)) {
				pending.remove(shopShelf);
			}
			fillFromAvailableStock();
		}));
		List<Node> route = new ArrayList<>();
		if (pickup != null) {
			route.add(pickup);
		}
		route.add(shopShelf.getShelfNode());
		if (pickup == null || pickup.equals(worker.currentNode)) {
			worker.showCarriedBox();
		}
		worker.setWalkingRoute(route);
		return planned;
	}

	/**
	 * Hoeveel artikelen één loop op dit schap zet: 25 procent van de maximale capaciteit.
	 * Past er minder bij, dan vult de vakkenvuller alleen die resterende plekken.
	 *
	 * @param shopShelf schap dat bijgevuld wordt
	 * @return aantal artikelen voor deze vulbeurt
	 */
	private int fillAmount(Shelf shopShelf) {
		int missing = shopShelf.getCapacity() - shopShelf.getCurrentStockCount();
		if (missing <= 0) {
			return 0;
		}
		int quarter = Math.max(1, shopShelf.getCapacity() / 4);
		return Math.min(missing, quarter);
	}

	/**
	 * Knoop van het magazijnschap waar dit winkelartikel nu ligt, zodat de vakkenvuller de dozen daar ophaalt.
	 *
	 * @param shopShelf schap dat dit artikel verkoopt
	 * @return de loopknoop, of null als er geen voorraad of geen knoop is
	 */
	private Node sourceShelf(Shelf shopShelf) {
		Product probe = shopShelf.createStockItem();
		if (probe == null) {
			return null;
		}
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			if (shelf.hasProduct(probe) && shelf.getShelfNode() != null) {
				return shelf.getShelfNode();
			}
		}
		return null;
	}

	/**
	 * Laat de vakkenvuller elk winkelschap bijvullen dat nog ruimte heeft
	 * en waarvoor in het magazijn voorraad ligt. Ze pakt één schap per loop.
	 * Zonder vakkenvuller gaat de voorraad meteen op het schap.
	 */
	public void fillFromAvailableStock() {
		if (warehouse == null) {
			return;
		}
		if (hasStockers() && idleStocker() == null) {
			restockQueued = true;
			return;
		}
		if (pumping) {
			restockQueued = true;
			return;
		}
		pumping = true;
		try {
			restockQueued = false;
			for (Shelf shop : shopShelves) {
				while (shop.getCurrentStockCount() < shop.getCapacity() && hasStockFor(shop)) {
					if (hasStockers() && idleStocker() == null) {
						restockQueued = true;
						return;
					}
					int moved = supplyShopShelf(shop);
					if (moved <= 0) {
						break;
					}
					if (!needsRestock(shop)) {
						pending.remove(shop);
					}
					if (hasStockers() && idleStocker() == null) {
						return;
					}
				}
			}
		} finally {
			pumping = false;
		}
		if (restockQueued && (!hasStockers() || idleStocker() != null)) {
			fillFromAvailableStock();
		}
	}

	/**
	 * Brengt de geplande dozen één voor één weg.
	 * Zonder medewerker of zonder loopknoop gaat de doos meteen op het schap.
	 *
	 * @param queue dozen die nog weg moeten
	 * @param after wat er gebeurt als elke doos op een magazijnschap staat
	 */
	private void transportBoxes(Queue<BoxDelivery> queue, Runnable after) {
		if (queue != null) {
			pendingBoxes.addAll(queue);
		}
		deliveryDone = after;
		dispatchBoxes();
	}

	/**
	 * Geeft elke vrije magazijnmedewerker één doos.
	 * Zonder medewerker of zonder loopknoop gaan de dozen meteen op het schap.
	 */
	private void dispatchBoxes() {
		if (employees.isEmpty() || dockNode == null) {
			WarehouseEmployee scanner = employees.isEmpty() ? null : employees.getFirst();
			while (!pendingBoxes.isEmpty()) {
				placeBox(pendingBoxes.remove(), scanner);
				if (boxStack != null) {
					boxStack.hideOne();
				}
			}
			finishDeliveryIfDone();
			return;
		}
		for (WarehouseEmployee worker : employees) {
			if (pendingBoxes.isEmpty()) {
				break;
			}
			if (worker.isBusy() || worker.sprite == null) {
				continue;
			}
			BoxDelivery box = pendingBoxes.remove();
			Node shelfNode = box.shelf().getShelfNode();
			if (shelfNode == null) {
				placeBox(box, worker);
				if (boxStack != null) {
					boxStack.hideOne();
				}
				continue;
			}
			beginCarry(worker, box, shelfNode);
		}
		finishDeliveryIfDone();
	}

	/**
	 * Laat deze medewerker leeg naar de stapel lopen, daar een doos pakken en die op het magazijnschap zetten.
	 *
	 * @param worker de magazijnmedewerker
	 * @param box de doos die hij brengt
	 * @param shelfNode knoop van het magazijnschap
	 */
	private void beginCarry(WarehouseEmployee worker, BoxDelivery box, Node shelfNode) {
		ImageView[] carried = new ImageView[1];
		worker.setOnMoved(() -> holdBox(worker, carried[0]));
		worker.setOnArrived(node -> {
			if (carried[0] == null && dockNode.equals(node)) {
				carried[0] = boxStack == null ? null : boxStack.detachTopBox();
				if (carried[0] != null) {
					carried[0].toFront();
				}
				holdBox(worker, carried[0]);
			}
		});
		worker.setOnRouteFinished(() -> worker.dwell(WorkDuration.PLACE_BOX_SECONDS, () -> {
			worker.setOnMoved(null);
			worker.setOnArrived(null);
			if (carried[0] != null) {
				carried[0].setVisible(false);
				if (boxStack != null) {
					boxStack.release(carried[0]);
				}
			}
			placeBox(box, worker);
			dispatchBoxes();
		}));
		if (dockNode.equals(worker.currentNode)) {
			carried[0] = boxStack == null ? null : boxStack.detachTopBox();
			if (carried[0] != null) {
				carried[0].toFront();
			}
			holdBox(worker, carried[0]);
		}
		worker.setWalkingRoute(List.of(dockNode, shelfNode));
	}

	/** Roept het vervolg aan als elke doos op een schap staat en niemand nog loopt. */
	private void finishDeliveryIfDone() {
		if (!pendingBoxes.isEmpty() || deliveryDone == null) {
			return;
		}
		for (WarehouseEmployee worker : employees) {
			if (worker.isBusy()) {
				return;
			}
		}
		Runnable done = deliveryDone;
		deliveryDone = null;
		for (WarehouseEmployee worker : employees) {
			worker.setOnMoved(null);
			worker.setOnArrived(null);
		}
		if (boxStack != null) {
			boxStack.collect();
		}
		done.run();
	}

	/** Houdt de doos in de handen van deze magazijnmedewerker, boven zijn hoofd. */
	private void holdBox(WarehouseEmployee worker, ImageView carried) {
		if (carried == null || worker == null || worker.sprite == null) {
			return;
		}
		carried.setX(worker.sprite.getCenterX() - 16);
		carried.setY(worker.sprite.getCenterY() - 36);
	}

	/**
	 * De medewerker scant de inhoud en het magazijnschap krijgt de artikelen.
	 *
	 * @param box doos die op het schap hoort
	 * @param carrier de medewerker die de doos bracht, of null
	 */
	private void placeBox(BoxDelivery box, WarehouseEmployee carrier) {
		if (carrier != null) {
			for (Product product : box.products()) {
				carrier.scanProduct(product);
			}
		}
		box.shelf().fillShelf(box.products());
		System.out.println("Doos met " + box.products().size() + " artikelen staat op " + box.shelf().getShelfId() + ".");
		// Zodra er voorraad ligt, gaan vrije vakkenvullers meteen aan de slag.
		fillFromAvailableStock();
	}

	/** Vult elk winkelschap dat nog op de wachtlijst staat. */
	private void fulfillPendingShelves() {
		for (Shelf shelf : new ArrayList<>(pending)) {
			supplyShopShelf(shelf);
			if (!needsRestock(shelf) || (hasStockers() && idleStocker() == null)) {
				pending.remove(shelf);
			}
		}
	}

	/**
	 * Bepaalt welke artikelen de vrachtwagen meeneemt en stopt die in dozen van twee.
	 * Zonder openstaande aanvraag komt er één doos van elk winkelartikel.
	 *
	 * @return de dozen, klaar om naar een schap gebracht te worden
	 */
	private List<BoxDelivery> planBoxes() {
		Map<String, Integer> demand = new LinkedHashMap<>();
		if (!pending.isEmpty()) {
			for (Shelf shop : pending) {
				Product sample = shop.createStockItem();
				if (sample == null || sample.getName() == null) {
					continue;
				}
				int missing = shop.getCapacity() - shop.getCurrentStockCount();
				int need = Math.max(0, missing - countByName(sample.getName()));
				if (need > 0) {
					demand.merge(sample.getName(), need, Integer::sum);
				}
			}
		}
		if (demand.isEmpty()) {
			for (Shelf shop : shopShelves) {
				Product sample = shop.createStockItem();
				if (sample != null && sample.getName() != null) {
					demand.putIfAbsent(sample.getName(), 2);
				}
			}
		}
		if (demand.isEmpty()) {
			demand.put("Magazijnproduct", warehouseFreeSpace());
		}

		List<Product> cargo = new ArrayList<>();
		int space = (warehouseFreeSpace() / ITEMS_PER_BOX) * ITEMS_PER_BOX;
		boolean added = true;
		while (space > 0 && added) {
			added = false;
			for (Map.Entry<String, Integer> entry : demand.entrySet()) {
				if (space == 0 || entry.getValue() <= 0) {
					continue;
				}
				cargo.add(createProduct(entry.getKey()));
				entry.setValue(entry.getValue() - 1);
				space--;
				added = true;
			}
		}
		return packOntoShelves(cargo);
	}

	/**
	 * Stopt de lading in dozen van {@link #ITEMS_PER_BOX} artikelen.
	 * Een magazijnschap kan meerdere van die dozen krijgen, zolang er telkens twee plekken vrij zijn.
	 *
	 * @param cargo alle artikelen die uit de vrachtwagen komen
	 * @return de dozen, elk met twee artikelen voor één magazijnschap
	 */
	private List<BoxDelivery> packOntoShelves(List<Product> cargo) {
		List<BoxDelivery> boxes = new ArrayList<>();
		Map<WarehouseShelf, Integer> reserved = new LinkedHashMap<>();
		int index = 0;
		boolean placed = true;
		while (placed && index + ITEMS_PER_BOX <= cargo.size()) {
			placed = false;
			for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
				if (index + ITEMS_PER_BOX > cargo.size()) {
					break;
				}
				int room = shelf.getCapacity() - shelf.getCurrentStockCount() - reserved.getOrDefault(shelf, 0);
				if (room < ITEMS_PER_BOX) {
					continue;
				}
				List<Product> box = new ArrayList<>();
				for (int i = 0; i < ITEMS_PER_BOX; i++) {
					box.add(cargo.get(index));
					index++;
				}
				reserved.merge(shelf, ITEMS_PER_BOX, Integer::sum);
				boxes.add(new BoxDelivery(shelf, box));
				placed = true;
			}
		}
		return boxes;
	}

	/**
	 * Maakt een nieuw exemplaar met deze naam, van het soort dat een winkelschap verkoopt.
	 *
	 * @param name naam van het artikel
	 * @return een nieuw exemplaar
	 */
	private Product createProduct(String name) {
		for (Shelf shelf : shopShelves) {
			Product sample = shelf.createStockItem();
			if (sample != null && name.equals(sample.getName())) {
				return shelf.createStockItem();
			}
		}
		return new Product("magazijn", name, 0);
	}

	/**
	 * Pakt exemplaren met de juiste naam van de magazijnschappen.
	 *
	 * @param shopShelf schap dat dit artikel verkoopt
	 * @param amount hoeveel exemplaren er maximaal mee mogen
	 * @return de gepakte exemplaren
	 */
	private List<Product> takeMatching(Shelf shopShelf, int amount) {
		List<Product> taken = new ArrayList<>();
		if (amount <= 0) {
			return taken;
		}
		Product probe = shopShelf.createStockItem();
		if (probe == null) {
			return taken;
		}
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			while (taken.size() < amount) {
				Product got = shelf.takeProduct(probe);
				if (got == null) {
					break;
				}
				taken.add(got);
			}
		}
		return taken;
	}

	/**
	 * Legt een artikel dat niet op de kar paste terug op het eerste magazijnschap met ruimte.
	 *
	 * @param product artikel dat terug moet
	 */
	private void returnToWarehouse(Product product) {
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			if (shelf.getCurrentStockCount() < shelf.getCapacity()) {
				shelf.fillShelf(List.of(product));
				return;
			}
		}
	}

	/**
	 * @param shopShelf winkelschap
	 * @return true als het magazijn nog vrije voorraad heeft die op dit schap hoort
	 */
	private boolean hasStockFor(Shelf shopShelf) {
		if (!hasStockers()) {
			return availableFor(shopShelf) > 0;
		}
		int planned = fillAmount(shopShelf);
		return planned > 0 && availableFor(shopShelf) >= planned;
	}

	/** @return true als er minstens één vakkenvuller met een kar is */
	private boolean hasStockers() {
		for (ShelfStocker stocker : stockers) {
			if (stocker != null && stocker.getTrolley() != null) {
				return true;
			}
		}
		return false;
	}

	/** @return een vakkenvuller die niet loopt en niet een schap aan het vullen is, of null */
	private ShelfStocker idleStocker() {
		for (ShelfStocker stocker : stockers) {
			if (stocker != null && stocker.getTrolley() != null && !stocker.isBusy()) {
				return stocker;
			}
		}
		return null;
	}

	/**
	 * Voorraad die nog niet voor een lopende vulbeurt is gereserveerd.
	 * Reserveren haalt de dozen nog niet van het schap.
	 *
	 * @param shopShelf winkelschap
	 * @return aantal artikelen dat een vakkenvuller nog mag inplannen
	 */
	private int availableFor(Shelf shopShelf) {
		Product sample = shopShelf.createStockItem();
		if (sample == null || sample.getName() == null) {
			return 0;
		}
		return Math.max(0, countByName(sample.getName()) - reservedStock.getOrDefault(sample.getName(), 0));
	}

	/** Houdt artikelen apart voor een vulbeurt, zonder ze al van het magazijnschap te halen. */
	private void reserve(String name, int amount) {
		if (name == null || amount <= 0) {
			return;
		}
		reservedStock.merge(name, amount, Integer::sum);
	}

	/** Geeft een reservering vrij, nadat de artikelen op het winkelschap zijn gezet. */
	private void release(String name, int amount) {
		if (name == null || amount <= 0) {
			return;
		}
		int left = reservedStock.getOrDefault(name, 0) - amount;
		if (left <= 0) {
			reservedStock.remove(name);
		} else {
			reservedStock.put(name, left);
		}
	}

	/** @return true als dit winkelschap nog op of onder zijn minimum zit */
	private boolean needsRestock(Shelf shelf) {
		return shelf.getCurrentStockCount() <= shelf.getMinimumInventory();
	}

	/** @return true als minstens één magazijnschap nog een artikel kan hebben */
	private boolean warehouseHasRoom() {
		return warehouseFreeSpace() > 0;
	}

	/** @return hoeveel artikelen er nog op alle magazijnschappen bij passen */
	private int warehouseFreeSpace() {
		int space = 0;
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			space += shelf.getCapacity() - shelf.getCurrentStockCount();
		}
		return space;
	}

	/** @return hoeveel exemplaren met deze naam er nu in het magazijn liggen */
	private int countByName(String name) {
		int count = 0;
		for (WarehouseShelf shelf : warehouse.getWarehouseShelves()) {
			for (Product product : shelf.getProducts()) {
				if (name.equals(product.getName())) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Eén doos: de artikelen en het magazijnschap waar ze naartoe moeten.
	 *
	 * @param shelf bestemmingschap
	 * @param products inhoud van de doos
	 */
	private record BoxDelivery(WarehouseShelf shelf, List<Product> products) {
	}
}
