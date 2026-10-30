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
 * Regelt het magazijn in drie stappen.
 * Een schap onder het minimum vraagt voorraad. De vrachtwagen brengt dozen van twee artikelen
 * naar de magazijnschappen. Vakkenvullers zetten daarvan een kwart van een winkelschap vol.
 * {@link Warehouse} bewaart de schappen en stuurt elke aanvraag hiernaartoe.
 */
public class WarehouseController {

	/** Artikelen in één doos die de vrachtwagen neerzet en die op een magazijnschap gaat. */
	static final int ITEMS_PER_BOX = 2;

	private Warehouse warehouse;
	private Truck truck;
	private BoxStack boxStack;
	private Node dockNode;

	private final List<WarehouseEmployee> employees = new ArrayList<>();
	private final List<ShelfStocker> stockers = new ArrayList<>();
	private final List<Shelf> shopShelves = new ArrayList<>();
	private final List<Shelf> shelvesBelowMinimum = new ArrayList<>();

	private final Queue<BoxDelivery> boxesToCarry = new ArrayDeque<>();
	private final Map<String, Integer> reservedStock = new HashMap<>();
	private Runnable afterUnloading;

	private boolean unloading;
	private boolean truckWaiting;
	private boolean fillingInProgress;
	private boolean fillRequestedWhileBusy;

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
	 * Voegt een magazijnmedewerker toe. Iedere medewerker draagt zijn eigen doos.
	 *
	 * @param employee de medewerker, mag null zijn
	 */
	public void addWarehouseEmployee(WarehouseEmployee employee) {
		if (employee != null && !employees.contains(employee)) {
			employees.add(employee);
		}
	}

	/**
	 * Voegt een vakkenvuller toe. Meerdere vakkenvullers vullen tegelijk verschillende schappen.
	 *
	 * @param stocker de vakkenvuller, mag null zijn
	 */
	public void addShelfStocker(ShelfStocker stocker) {
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
		rememberShelf(shelf);
		System.out.println("Drempelwaarde bereikt voor " + shelf.getShelfId() + ". Voorraad komt uit het magazijn.");
		if (stockersAreAllBusy() && hasEnoughForATrip(shelf)) {
			fillRequestedWhileBusy = true;
			return;
		}
		int moved = restockShopShelf(shelf);
		if (moved > 0 || !needsRestock(shelf)) {
			dropShelfIfRestocked(shelf);
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
	 * De vrachtwagen is aangekomen. Elke doos gaat naar een magazijnschap.
	 * Daarna vullen de vakkenvullers de winkelschappen die om voorraad vroegen.
	 *
	 * @param arrived de aangekomen vrachtwagen, mag null zijn als de dozen al gepland zijn
	 */
	public void receiveTruck(Truck arrived) {
		if (warehouse == null) {
			return;
		}
		if (unloading) {
			truckWaiting = true;
			return;
		}
		unloading = true;
		System.out.println("Vrachtwagen is aangekomen. Dozen gaan naar de magazijnschappen.");
		List<BoxDelivery> boxes = planBoxes();
		if (boxStack != null) {
			boxStack.show(boxes.size());
		}
		startUnloading(boxes, () -> {
			unloading = false;
			restockShelvesBelowMinimum();
			fillFromAvailableStock();
			if (truckWaiting) {
				truckWaiting = false;
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
				if (sameProduct(product, productId)) {
					found.add(product);
				}
			}
		}
		for (Product product : warehouse.getLooseStock()) {
			if (sameProduct(product, productId)) {
				found.add(product);
			}
		}
		return found;
	}

	/**
	 * Laat een vrije vakkenvuller elk winkelschap bijvullen dat nog ruimte heeft
	 * en waarvoor in het magazijn genoeg voorraad ligt.
	 * Zonder vakkenvuller gaat de voorraad meteen op het schap.
	 */
	public void fillFromAvailableStock() {
		if (warehouse == null) {
			return;
		}
		if (stockersAreAllBusy() || fillingInProgress) {
			fillRequestedWhileBusy = true;
			return;
		}
		fillingInProgress = true;
		try {
			fillRequestedWhileBusy = false;
			for (Shelf shop : shopShelves) {
				fillShelfUntilBlocked(shop);
				if (stockersAreAllBusy()) {
					return;
				}
			}
		} finally {
			fillingInProgress = false;
		}
		if (fillRequestedWhileBusy && !stockersAreAllBusy()) {
			fillFromAvailableStock();
		}
	}

	/**
	 * Vult dit schap zolang er ruimte en genoeg magazijnvoorraad is.
	 * Stopt zodra een vulbeurt niets meer verzet of alle vakkenvullers bezig zijn.
	 *
	 * @param shop winkelschap
	 */
	private void fillShelfUntilBlocked(Shelf shop) {
		while (shop.getCurrentStockCount() < shop.getCapacity() && hasEnoughForATrip(shop)) {
			if (stockersAreAllBusy()) {
				fillRequestedWhileBusy = true;
				return;
			}
			int moved = restockShopShelf(shop);
			if (moved <= 0) {
				return;
			}
			dropShelfIfRestocked(shop);
			if (stockersAreAllBusy()) {
				fillRequestedWhileBusy = true;
				return;
			}
		}
	}

	/**
	 * Zet voorraad van de magazijnschappen op één winkelschap.
	 * Een vrije vakkenvuller loopt erheen. Zonder vakkenvuller gaat de voorraad meteen op het schap.
	 *
	 * @param shopShelf schap in de winkel
	 * @return hoeveel artikelen er op de kar of op het schap zijn gezet
	 */
	private int restockShopShelf(Shelf shopShelf) {
		if (shopShelf == null || shopShelf instanceof WarehouseShelf) {
			return 0;
		}
		if (stockersAreAllBusy()) {
			fillRequestedWhileBusy = true;
			return 0;
		}
		int missing = shopShelf.getCapacity() - shopShelf.getCurrentStockCount();
		if (missing <= 0) {
			return 0;
		}
		ShelfStocker worker = idleStocker();
		if (worker == null || worker.getTrolley() == null || shopShelf.getShelfNode() == null) {
			return moveStockImmediately(shopShelf, missing);
		}
		return sendStocker(worker, shopShelf);
	}

	/**
	 * Legt de magazijnvoorraad meteen op het winkelschap, zonder dat iemand loopt.
	 *
	 * @param shopShelf schap in de winkel
	 * @param missing hoeveel plekken er nog vrij zijn
	 * @return hoeveel artikelen er echt op het schap zijn gezet
	 */
	private int moveStockImmediately(Shelf shopShelf, int missing) {
		int amount = Math.min(missing, availableFor(shopShelf));
		List<Product> taken = takeMatching(shopShelf, amount);
		if (taken.isEmpty()) {
			return 0;
		}
		return shopShelf.fillShelf(taken);
	}

	/**
	 * Stuurt één vakkenvuller naar het magazijnschap en daarna naar het winkelschap.
	 * De artikelen blijven op het magazijnschap tot het winkelschap gevuld is.
	 *
	 * @param worker vrije vakkenvuller met een kar
	 * @param shopShelf schap dat bijgevuld wordt
	 * @return aantal artikelen van deze vulbeurt, of 0 als die niet start
	 */
	private int sendStocker(ShelfStocker worker, Shelf shopShelf) {
		int planned = fillAmount(shopShelf);
		if (planned <= 0 || availableFor(shopShelf) < planned || worker.getTrolley().remainingSpace() < planned) {
			return 0;
		}
		String stockName = productName(shopShelf);
		reserve(stockName, planned);
		Node pickup = pickupNode(shopShelf);
		System.out.println(worker.getRoleLabel() + " haalt " + planned + " artikelen voor " + shopShelf.getShelfId() + ".");
		worker.setOnArrived(node -> {
			if (pickup == null || pickup.equals(node)) {
				worker.showCarriedBox();
			}
		});
		worker.setOnRouteFinished(() -> worker.dwell(WorkDuration.placeProducts(planned), () -> {
			worker.hideCarriedBox();
			List<Product> taken = takeMatching(shopShelf, planned);
			release(stockName, planned);
			for (Product product : taken) {
				worker.getTrolley().addProduct(product);
			}
			worker.fillShelf(shopShelf);
			dropShelfIfRestocked(shopShelf);
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
	 * Knoop van het magazijnschap waar dit winkelartikel nu ligt.
	 *
	 * @param shopShelf schap dat dit artikel verkoopt
	 * @return de loopknoop, of null als er geen voorraad of geen knoop is
	 */
	private Node pickupNode(Shelf shopShelf) {
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

	/** Vult elk winkelschap dat nog op de wachtlijst staat. */
	private void restockShelvesBelowMinimum() {
		for (Shelf shelf : new ArrayList<>(shelvesBelowMinimum)) {
			restockShopShelf(shelf);
			if (!needsRestock(shelf) || stockersAreAllBusy()) {
				shelvesBelowMinimum.remove(shelf);
			}
		}
	}

	/** Geeft de geplande dozen aan de magazijnmedewerkers. */
	private void startUnloading(List<BoxDelivery> boxes, Runnable after) {
		boxesToCarry.addAll(boxes);
		afterUnloading = after;
		assignBoxes();
	}

	/**
	 * Geeft elke vrije magazijnmedewerker één doos.
	 * Zonder medewerker of zonder laadklep gaan de dozen meteen op het schap.
	 */
	private void assignBoxes() {
		if (employees.isEmpty() || dockNode == null) {
			placeBoxesWithoutWalking();
			finishUnloadingIfDone();
			return;
		}
		for (WarehouseEmployee worker : employees) {
			if (boxesToCarry.isEmpty()) {
				break;
			}
			if (worker.isBusy() || worker.sprite == null) {
				continue;
			}
			BoxDelivery box = boxesToCarry.remove();
			Node shelfNode = box.shelf().getShelfNode();
			if (shelfNode == null) {
				putBoxOnWarehouseShelf(box, worker);
				hideOneStackedBox();
				continue;
			}
			carryBoxToShelf(worker, box, shelfNode);
		}
		finishUnloadingIfDone();
	}

	/** Zet elke resterende doos meteen op zijn magazijnschap. */
	private void placeBoxesWithoutWalking() {
		WarehouseEmployee scanner = employees.isEmpty() ? null : employees.getFirst();
		while (!boxesToCarry.isEmpty()) {
			putBoxOnWarehouseShelf(boxesToCarry.remove(), scanner);
			hideOneStackedBox();
		}
	}

	/**
	 * Laat deze medewerker leeg naar de stapel lopen, daar een doos pakken en die op het magazijnschap zetten.
	 *
	 * @param worker de magazijnmedewerker
	 * @param box de doos die hij brengt
	 * @param shelfNode knoop van het magazijnschap
	 */
	private void carryBoxToShelf(WarehouseEmployee worker, BoxDelivery box, Node shelfNode) {
		CarriedBox carried = new CarriedBox();
		worker.setOnMoved(() -> holdBox(worker, carried.image));
		worker.setOnArrived(node -> {
			if (dockNode.equals(node)) {
				pickUpBox(worker, carried);
			}
		});
		worker.setOnRouteFinished(() -> worker.dwell(WorkDuration.PLACE_BOX_SECONDS, () -> {
			worker.setOnMoved(null);
			worker.setOnArrived(null);
			dropCarriedBox(carried);
			putBoxOnWarehouseShelf(box, worker);
			assignBoxes();
		}));
		if (dockNode.equals(worker.currentNode)) {
			pickUpBox(worker, carried);
		}
		worker.setWalkingRoute(List.of(dockNode, shelfNode));
	}

	/** Pakt de bovenste doos van de stapel, alleen als deze medewerker er nog geen vasthoudt. */
	private void pickUpBox(WarehouseEmployee worker, CarriedBox carried) {
		if (carried.image != null) {
			return;
		}
		carried.image = boxStack == null ? null : boxStack.detachTopBox();
		if (carried.image != null) {
			carried.image.toFront();
		}
		holdBox(worker, carried.image);
	}

	/** Legt de gedragen doos weg zodra die op het magazijnschap staat. */
	private void dropCarriedBox(CarriedBox carried) {
		if (carried.image == null) {
			return;
		}
		carried.image.setVisible(false);
		if (boxStack != null) {
			boxStack.release(carried.image);
		}
	}

	/** Houdt de doos in de handen van deze magazijnmedewerker, boven zijn hoofd. */
	private void holdBox(WarehouseEmployee worker, ImageView carried) {
		if (carried == null || worker == null || worker.sprite == null) {
			return;
		}
		carried.setX(worker.sprite.getCenterX() - 16);
		carried.setY(worker.sprite.getCenterY() - 36);
	}

	/** Haalt één zichtbare doos van de stapel, als die stapel er is. */
	private void hideOneStackedBox() {
		if (boxStack != null) {
			boxStack.hideOne();
		}
	}

	/**
	 * De medewerker scant de inhoud en het magazijnschap krijgt de artikelen.
	 * Vrije vakkenvullers gaan daarna meteen aan de slag.
	 *
	 * @param box doos die op het schap hoort
	 * @param carrier de medewerker die de doos bracht, of null
	 */
	private void putBoxOnWarehouseShelf(BoxDelivery box, WarehouseEmployee carrier) {
		if (carrier != null) {
			for (Product product : box.products()) {
				carrier.scanProduct(product);
			}
		}
		box.shelf().fillShelf(box.products());
		System.out.println("Doos met " + box.products().size() + " artikelen staat op " + box.shelf().getShelfId() + ".");
		fillFromAvailableStock();
	}

	/** Roept het vervolg aan als elke doos op een schap staat en niemand nog loopt. */
	private void finishUnloadingIfDone() {
		if (!boxesToCarry.isEmpty() || afterUnloading == null || anEmployeeIsBusy()) {
			return;
		}
		Runnable done = afterUnloading;
		afterUnloading = null;
		for (WarehouseEmployee worker : employees) {
			worker.setOnMoved(null);
			worker.setOnArrived(null);
		}
		if (boxStack != null) {
			boxStack.collect();
		}
		done.run();
	}

	/** @return true als een magazijnmedewerker nog loopt of een doos aan het wegzetten is */
	private boolean anEmployeeIsBusy() {
		for (WarehouseEmployee worker : employees) {
			if (worker.isBusy()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Bepaalt welke artikelen de vrachtwagen meeneemt en stopt die in dozen van twee.
	 * Zonder openstaande aanvraag komt er één doos van elk winkelartikel.
	 *
	 * @return de dozen, klaar om naar een schap gebracht te worden
	 */
	private List<BoxDelivery> planBoxes() {
		return packOntoShelves(loadCargo(demandedProducts()));
	}

	/**
	 * Hoeveel van elk artikel de vrachtwagen nog moet brengen.
	 * Eerst de schappen onder het minimum, anders één doos van elk winkelartikel.
	 *
	 * @return naam naar aantal artikelen
	 */
	private Map<String, Integer> demandedProducts() {
		Map<String, Integer> demand = new LinkedHashMap<>();
		for (Shelf shop : shelvesBelowMinimum) {
			String name = productName(shop);
			if (name == null) {
				continue;
			}
			int missing = shop.getCapacity() - shop.getCurrentStockCount();
			int need = Math.max(0, missing - countByName(name));
			if (need > 0) {
				demand.merge(name, need, Integer::sum);
			}
		}
		if (!demand.isEmpty()) {
			return demand;
		}
		for (Shelf shop : shopShelves) {
			String name = productName(shop);
			if (name != null) {
				demand.putIfAbsent(name, ITEMS_PER_BOX);
			}
		}
		if (demand.isEmpty()) {
			demand.put("Magazijnproduct", warehouseFreeSpace());
		}
		return demand;
	}

	/**
	 * Maakt de artikelen die in de dozen gaan, om de beurt per naam, tot het magazijn vol is.
	 *
	 * @param demand naam naar aantal dat nog nodig is
	 * @return de lading, in de volgorde waarin ze ingepakt wordt
	 */
	private List<Product> loadCargo(Map<String, Integer> demand) {
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
		return cargo;
	}

	/**
	 * Stopt de lading in dozen van {@link #ITEMS_PER_BOX} artikelen.
	 * Een magazijnschap krijgt alleen een doos als er twee plekken vrij zijn.
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
			if (name.equals(productName(shelf))) {
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
	 * @param shopShelf winkelschap
	 * @return true als het magazijn genoeg vrije voorraad heeft voor een vulbeurt
	 */
	private boolean hasEnoughForATrip(Shelf shopShelf) {
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

	/** @return true als er vakkenvullers zijn en geen van hen vrij is */
	private boolean stockersAreAllBusy() {
		return hasStockers() && idleStocker() == null;
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
		String name = productName(shopShelf);
		if (name == null) {
			return 0;
		}
		return Math.max(0, countByName(name) - reservedStock.getOrDefault(name, 0));
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

	/** Onthoudt een winkelschap dat op of onder zijn minimum zit. */
	private void rememberShelf(Shelf shelf) {
		if (!shelvesBelowMinimum.contains(shelf)) {
			shelvesBelowMinimum.add(shelf);
		}
	}

	/** Haalt het schap van de wachtlijst als het weer boven het minimum zit. */
	private void dropShelfIfRestocked(Shelf shelf) {
		if (!needsRestock(shelf)) {
			shelvesBelowMinimum.remove(shelf);
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
	 * @param shopShelf winkelschap
	 * @return de verkoopnaam van het artikel op dit schap, of null
	 */
	private String productName(Shelf shopShelf) {
		if (shopShelf == null) {
			return null;
		}
		Product sample = shopShelf.createStockItem();
		if (sample == null) {
			return null;
		}
		return sample.getName();
	}

	/** @return true als dit artikel het gezochte id of dezelfde naam heeft */
	private boolean sameProduct(Product product, String productId) {
		return productId.equals(product.getId()) || productId.equals(product.getName());
	}

	/**
	 * Eén doos: de artikelen en het magazijnschap waar ze naartoe moeten.
	 *
	 * @param shelf bestemmingschap
	 * @param products inhoud van de doos
	 */
	private record BoxDelivery(WarehouseShelf shelf, List<Product> products) {
	}

	/** De doos die een magazijnmedewerker nu in zijn handen heeft. */
	private static final class CarriedBox {
		private ImageView image;
	}
}
