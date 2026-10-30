package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.controllers.SupermarketController;
import com.example.schoolsupermarktsimulator.controllers.WarehouseController;
import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Cashier;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.people.Manager;
import com.example.schoolsupermarktsimulator.objects.people.ShelfStocker;
import com.example.schoolsupermarktsimulator.objects.people.WarehouseEmployee;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.registers.CashRegister;
import com.example.schoolsupermarktsimulator.objects.registers.Register;
import com.example.schoolsupermarktsimulator.objects.registers.SelfScanRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingBasket;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingCart;
import com.example.schoolsupermarktsimulator.objects.shopping.ShoppingCartFigure;
import com.example.schoolsupermarktsimulator.objects.shopping.StockTrolley;
import com.example.schoolsupermarktsimulator.objects.shopping.StockTrolleyFigure;
import com.example.schoolsupermarktsimulator.view.NodeMapView;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Bouwt de winkel: plattegrond, personen, kassa's en het magazijn.
 * Klanten lopen langs de schappen die op hun lijst staan, pakken artikelen in een mand of wagen
 * en rekenen af. In de winkel lopen ongeveer tien klanten tegelijk.
 */
public class GameWorld {

    private static final String[] CUSTOMER_NAMES = {
            "Bo", "Tess", "Milan", "Noa", "Sem", "Eva", "Luuk", "Fleur",
            "Daan", "Isa", "Finn", "Sofie", "Jens", "Roos", "Mees", "Lotte"
    };
    private static final String[] GROCERIES = {
            "Meloen", "Kool", "Pompoen", "Pasta", "Snacks", "Eieren",
            "Jam", "Snoep", "Cola", "Sap", "Frisdrank", "Brood"
    };
    private static final Color[] SHIRTS = {
            Color.CRIMSON, Color.TEAL, Color.SEAGREEN, Color.CHOCOLATE,
            Color.DARKSLATEBLUE, Color.OLIVEDRAB, Color.MEDIUMVIOLETRED
    };

    private final Pane world;
    private final Pane pathOverlay;
    private final StoreFloorView floor;
    private final NodeMap nodeMap;
    private final SupermarketController supermarket = new SupermarketController();
    private final Node exit;
    private final Cashier cashier;
    private final CashRegister cashRegister;
    private static final int TARGET_CUSTOMERS = 10;

    private final SelfScanRegister selfScan;
    private int nextCustomerId = 9;
    private int spawnCount;
    private int customersInside;

    public GameWorld() {
        world = new Pane();

        Background background = new Background(16, 9);
        floor = new StoreFloorView();

        world.getChildren().add(background.getView());
        world.getChildren().add(floor.getView());

        nodeMap = new NodeMap();
        pathOverlay = NodeMapView.create(nodeMap.getNodes());
        world.getChildren().add(pathOverlay);
        floor.bindShelfNodes(nodeMap.getNodes());
        // De twee magazijnschappen krijgen elk hun eigen knoop, anders lopen beide dozen naar dezelfde plek.
        List<WarehouseShelf> warehouseShelves = floor.getWarehouseShelves();
        if (!warehouseShelves.isEmpty()) {
            warehouseShelves.getFirst().setShelfNode(nodeMap.getNode(51));
        }
        if (warehouseShelves.size() > 1) {
            warehouseShelves.get(1).setShelfNode(nodeMap.getNode(50));
        }

        WarehouseController warehouseController = floor.getWarehouseController();
        warehouseController.setDockNode(nodeMap.getNode(59));

        WarehouseEmployee piet = new WarehouseEmployee(nodeMap.getNode(57), 3, "Piet");
        WarehouseEmployee kees = new WarehouseEmployee(nodeMap.getNode(56), 7, "Kees");
        kees.setShirt(Color.MEDIUMPURPLE.darker());
        world.getChildren().add(piet.sprite);
        world.getChildren().add(kees.sprite);
        warehouseController.setWarehouseEmployee(piet);
        warehouseController.setWarehouseEmployee(kees);

        ShelfStocker inge = new ShelfStocker(nodeMap.getNode(48), 4, "Inge");
        ShelfStocker bram = new ShelfStocker(nodeMap.getNode(52), 8, "Bram");
        bram.setShirt(Color.DARKORANGE.darker());
        inge.attachTrolley(new StockTrolley());
        bram.attachTrolley(new StockTrolley());
        inge.carry(new StockTrolleyFigure(), 30, 8);
        bram.carry(new StockTrolleyFigure(), 30, 8);
        world.getChildren().add(inge.sprite);
        world.getChildren().add(bram.sprite);
        warehouseController.setShelfStocker(inge);
        warehouseController.setShelfStocker(bram);

        // Sara staat naast de stoel bij de kassa, niet in de klantenrij.
        cashier = new Cashier(nodeMap.getNode(35), 2, "Sara");
        cashier.setPosition(1112, 448);
        world.getChildren().add(cashier.sprite);
        cashRegister = floor.getCashRegisters().getFirst();
        // Vooraan bij de band, daarna twee wachtplekken naar links.
        cashRegister.setQueueSpots(List.of(
                nodeMap.getNode(33),
                nodeMap.getNode(32),
                nodeMap.getNode(31)
        ));
        cashRegister.assignCashier(cashier);

        selfScan = floor.getSelfScanRegisters().getFirst();
        selfScan.setQueueSpots(List.of(
                nodeMap.getNode(39),
                nodeMap.getNode(38),
                nodeMap.getNode(37)
        ));

        Manager noor = new Manager(nodeMap.getNode(2), 6, "Noor");
        world.getChildren().add(noor.sprite);
        noor.startRound(List.of(
                nodeMap.getNode(4),
                nodeMap.getNode(8),
                nodeMap.getNode(17),
                nodeMap.getNode(22),
                nodeMap.getNode(30),
                nodeMap.getNode(36),
                nodeMap.getNode(11)
        ));
        supermarket.setManager(noor);

        placeCartCorral();
        placeWarehouseTrolley();
        piet.sprite.toFront();
        kees.sprite.toFront();
        inge.sprite.toFront();
        bram.sprite.toFront();
        cashier.sprite.toFront();
        noor.sprite.toFront();

        exit = nodeMap.getNode(46);

        // Joop heeft een mand en loopt naar de meloenen, daarna de zelfscan.
        Customer joop = new Customer(nodeMap.getNode(0), 1, "Joop");
        joop.setShirt(Color.RED);
        joop.setBudget(30);
        joop.attachAid(new ShoppingBasket());
        joop.setGroceryList(List.of(new Product("Meloen")));
        addCustomer(joop, selfScan);

        // Anne duwt een wagen en rekent af bij de gewone kassa, waar Sara scant.
        Customer anne = new Customer(nodeMap.getNode(0), 5, "Anne");
        anne.setShirt(Color.CRIMSON);
        anne.setBudget(40);
        anne.attachAid(new ShoppingCart());
        anne.carry(new ShoppingCartFigure(), 24, 10);
        anne.setGroceryList(List.of(new Product("Pasta")));
        addCustomer(anne, cashRegister);

        // Dozen komen binnen. Piet en Kees brengen ze naar de magazijnschappen.
        // Inge en Bram vullen de schappen zodra er voorraad in het magazijn ligt.
        warehouseController.orderTruck();
        warehouseController.fillFromAvailableStock();
        keepCustomerCount();
        pathOverlay.toFront();
    }

    /**
     * Zet een rij winkelwagens net binnen de deur, onder het bovenpad.
     */
    private void placeCartCorral() {
        for (int i = 0; i < 4; i++) {
            ShoppingCartFigure cart = new ShoppingCartFigure();
            cart.setCenter(1148 + i * 22, 246);
            world.getChildren().add(cart);
        }
    }

    /**
     * Zet een vulkar in het magazijn, naast de dozen van de vrachtwagen.
     */
    private void placeWarehouseTrolley() {
        StockTrolleyFigure trolley = new StockTrolleyFigure();
        trolley.setCenter(300, 500);
        world.getChildren().add(trolley);
    }

    /**
     * Houdt het aantal klanten in de winkel rond {@link #TARGET_CUSTOMERS}.
     * Vertrekt er één, dan komt er een nieuwe door de deur.
     */
    private void keepCustomerCount() {
        while (customersInside < TARGET_CUSTOMERS) {
            spawnCustomer();
        }
    }

    /**
     * Maakt een klant buiten de deur, met een mand of een wagen en één artikel op de lijst.
     */
    private void spawnCustomer() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String name = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
        Customer customer = new Customer(nodeMap.getNode(0), nextCustomerId++, name);
        customer.setShirt(SHIRTS[random.nextInt(SHIRTS.length)]);
        customer.setBudget(30);
        boolean usesCart = spawnCount % 2 == 0;
        if (usesCart) {
            customer.attachAid(new ShoppingCart());
            customer.carry(new ShoppingCartFigure(), 24, 10);
        } else {
            customer.attachAid(new ShoppingBasket());
        }
        customer.setGroceryList(List.of(new Product(GROCERIES[random.nextInt(GROCERIES.length)])));
        addCustomer(customer, chooseRegister(random));
        spawnCount++;
    }

    /**
     * Kiest bij binnenkomst of deze klant bij de zelfscan of bij de bemande kassa afrekent.
     * Mand of wagen staat daar los van: beide kunnen naar elke kassa.
     *
     * @param random generator voor deze spawn
     * @return de kassa die deze klant wil gebruiken
     */
    private Register chooseRegister(ThreadLocalRandom random) {
        boolean wantsSelfScan = random.nextBoolean();
        return wantsSelfScan ? selfScan : cashRegister;
    }

    /**
     * Zet de klant in de winkel en haalt het figuur weg zodra die buiten is.
     *
     * @param customer klant die naar binnen loopt
     * @param register kassa waar deze klant betaalt
     */
    private void addCustomer(Customer customer, Register register) {
        world.getChildren().add(customer.sprite);
        customer.sprite.toFront();
        pathOverlay.toFront();
        customersInside++;
        supermarket.beginVisit(customer, floor.getAllShelves(), register, cashier, exit, () -> {
            world.getChildren().remove(customer.sprite);
            customersInside--;
            if (customersInside < TARGET_CUSTOMERS) {
                spawnCustomer();
            }
        });
    }

    public Pane getView() {
        return world;
    }
}
