package com.example.schoolsupermarktsimulator.controllers;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.people.Cashier;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.people.Manager;
import com.example.schoolsupermarktsimulator.objects.people.Person;
import com.example.schoolsupermarktsimulator.objects.people.WorkDuration;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.registers.Register;
import com.example.schoolsupermarktsimulator.objects.registers.SelfScanRegister;
import com.example.schoolsupermarktsimulator.objects.shelves.Shelf;
import com.example.schoolsupermarktsimulator.objects.shelves.WarehouseShelf;
import com.example.schoolsupermarktsimulator.objects.shopping.MoveableInventory;
import com.example.schoolsupermarktsimulator.view.SimulationStage;
import com.example.schoolsupermarktsimulator.view.StoreFloorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SupermarketController {

    private boolean isPaused;

    private Manager manager;

    private final Map<Customer, Visit> visits = new HashMap<>();

    private Shelf[] shelf;

    private StoreFloorView storeFloorView;

    private SimulationStage simulationStage;

    private Product product;

    private Register[] register;

    private Person[] person;

    private MoveableInventory moveableInventory;

    /**
     * De manager loopt een ronde tot een zelfscan om een steekproef vraagt.
     *
     * @param manager de manager van deze winkel
     */
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void startSimulation() {

    }

    public void pauseSimulation() {

    }

    public void updateSimulation() {

    }

    /**
     * Zoekt de loopknopen van de schappen waar deze klant iets van de boodschappenlijst kan pakken.
     * Magazijnschappen tellen niet mee: daar winkelt een klant niet.
     *
     * @param customer klant met een boodschappenlijst
     * @param allShelves alle schappen op de plattegrond
     * @return de knopen, in de volgorde van de schappen
     */
    public List<Node> findRequiredNodes(Customer customer, List<Shelf> allShelves) {
        if (customer == null || customer.getGroceryList() == null || allShelves == null) {
            return List.of();
        }
        // Ga door alle schappen, per schap alle producten, en kijk of een naam op de boodschappenlijst staat.
        return allShelves.stream()
                .filter(shelf -> !(shelf instanceof WarehouseShelf))
                .filter(shelf -> shelf.getShelfNode() != null)
                .filter(shelf ->
                        shelf.getProducts().stream()
                                .anyMatch(hasProduct ->
                                        customer.getGroceryList().stream()
                                                .anyMatch(wantsProduct ->
                                                        wantsProduct.getName() != null
                                                                && wantsProduct.getName().equals(hasProduct.getName())
                                                )
                                )
                )
                .map(Shelf::getShelfNode)
                .collect(Collectors.toList());
    }

    /**
     * Laat een klant langs de juiste schappen lopen, daar artikelen pakken, afrekenen en de winkel verlaten.
     * Bij een gewone kassa scant de gekoppelde medewerker. Bij een zelfscan is inspector alleen nodig
     * als de steekproef afgaat.
     *
     * @param customer klant met mand of wagen en een boodschappenlijst
     * @param shelves alle schappen
     * @param register kassa waar de klant betaalt
     * @param inspector kassamedewerker voor een zelfscan-steekproef
     * @param exit knoop buiten de winkel, of null als de klant blijft staan
     * @param afterLeave wordt aangeroepen als de klant buiten is, zodat het figuur weggehaald kan worden
     */
    public void beginVisit(Customer customer, List<Shelf> shelves, Register register, Cashier inspector, Node exit, Runnable afterLeave) {
        if (customer == null) {
            return;
        }
        List<Node> route = new ArrayList<>();
        for (Node node : findRequiredNodes(customer, shelves)) {
            if (node != null && !route.contains(node)) {
                route.add(node);
            }
        }
        visits.put(customer, new Visit(register, inspector, exit, afterLeave));
        customer.setOnArrived(node -> takeFromShelvesAt(customer, shelves, node));
        customer.setOnRouteFinished(() -> enterQueue(customer));
        customer.setWalkingRoute(route);
    }

    /**
     * Sluit achteraan aan en schuift door tot de kassa vrij is.
     * Alleen de voorste klant rekent af. De rest blijft op een wachtplek staan.
     *
     * @param customer klant die klaar is met winkelen
     */
    private void enterQueue(Customer customer) {
        Visit visit = visits.get(customer);
        if (visit == null) {
            return;
        }
        customer.setOnArrived(null);
        if (visit.register == null) {
            finishVisit(customer);
            return;
        }
        visit.register.joinQueue(customer);
        arrangeQueue(visit.register);
    }

    /**
     * Zet elke klant op de juiste plek in de rij. Wie vooraan staat en daar is aangekomen, mag afrekenen.
     *
     * @param register kassa waarvan de rij opnieuw wordt neergezet
     */
    private void arrangeQueue(Register register) {
        if (register == null) {
            return;
        }
        List<Customer> line = register.waitingLine();
        for (int index = 0; index < line.size(); index++) {
            Customer customer = line.get(index);
            Visit visit = visits.get(customer);
            if (visit == null || visit.paying) {
                continue;
            }
            Node spot = register.spotForIndex(index);
            boolean standingThere = spot != null && spot.equals(customer.currentNode) && !customer.isBusy();
            if (standingThere) {
                if (customer == register.getCurrentCustomer()) {
                    startPayment(customer);
                }
                continue;
            }
            customer.setOnRouteFinished(() -> {
                if (customer == register.getCurrentCustomer()) {
                    startPayment(customer);
                }
            });
            if (spot != null) {
                customer.setWalkingRoute(List.of(spot));
            }
        }
    }

    /**
     * De voorste klant blijft staan om te betalen. Daarna schuift de rij op.
     *
     * @param customer klant die aan de beurt is
     */
    private void startPayment(Customer customer) {
        Visit visit = visits.get(customer);
        if (visit == null || visit.paying || visit.register == null) {
            return;
        }
        if (customer != visit.register.getCurrentCustomer()) {
            return;
        }
        visit.paying = true;
        int items = customer.getAid() == null ? 0 : customer.getAid().getProducts().size();
        customer.dwell(WorkDuration.checkout(items), () -> completePayment(customer));
    }

    /**
     * Rekent de voorste klant af en laat de rest een plek opschuiven.
     *
     * @param customer klant die klaar is met wachten
     */
    private void completePayment(Customer customer) {
        Visit visit = visits.get(customer);
        if (visit == null || visit.register == null) {
            return;
        }
        Register register = visit.register;
        if (register instanceof SelfScanRegister self && manager != null && self.rollInspection()) {
            Node where = register.spotForIndex(0);
            if (where == null) {
                where = customer.currentNode;
            }
            manager.answerCall(where, () -> {
                self.holdInspection(true);
                register.completeCheckout(customer, manager);
                finishVisit(customer);
                arrangeQueue(register);
            });
            return;
        }
        if (register instanceof SelfScanRegister self) {
            self.holdInspection(false);
        }
        register.completeCheckout(customer, visit.inspector);
        finishVisit(customer);
        arrangeQueue(register);
    }

    /**
     * De klant verlaat de winkel. De rijgegevens van deze klant gaan mee weg.
     *
     * @param customer klant die buiten loopt
     */
    private void finishVisit(Customer customer) {
        Visit visit = visits.remove(customer);
        if (visit == null) {
            return;
        }
        visit.paying = false;
        leave(customer, visit.exit, visit.afterLeave);
    }

    /**
     * Laat de klant naar de uitgang lopen en haalt het figuur daarna weg.
     *
     * @param customer klant die klaar is met betalen
     * @param exit knoop buiten de winkel, of null
     * @param afterLeave wat er gebeurt buiten, mag null zijn
     */
    private void leave(Customer customer, Node exit, Runnable afterLeave) {
        if (exit != null) {
            if (afterLeave != null) {
                customer.setOnRouteFinished(afterLeave);
            }
            customer.setWalkingRoute(List.of(exit));
        } else if (afterLeave != null) {
            afterLeave.run();
        }
    }

    /**
     * Blijft bij elk schap staan waar iets van de lijst ligt, en pakt dat daarna.
     *
     * @param customer klant die net is aangekomen
     * @param shelves schappen die die knoop kunnen delen
     * @param node de knoop waar de klant staat
     */
    private void takeFromShelvesAt(Customer customer, List<Shelf> shelves, Node node) {
        if (node == null || shelves == null) {
            return;
        }
        List<Shelf> here = new ArrayList<>();
        for (Shelf shelf : shelves) {
            if (node.equals(shelf.getShelfNode()) && customer.wantsFrom(shelf)) {
                here.add(shelf);
            }
        }
        if (here.isEmpty()) {
            return;
        }
        customer.dwellBeforeContinue(here.size() * WorkDuration.TAKE_FROM_SHELF_SECONDS, () -> {
            for (Shelf shelf : here) {
                customer.takeFromShelf(shelf);
            }
        });
    }

    /**
     * Wat deze klant nodig heeft om af te rekenen en daarna de winkel te verlaten.
     */
    private static final class Visit {
        private final Register register;
        private final Cashier inspector;
        private final Node exit;
        private final Runnable afterLeave;
        private boolean paying;

        private Visit(Register register, Cashier inspector, Node exit, Runnable afterLeave) {
            this.register = register;
            this.inspector = inspector;
            this.exit = exit;
            this.afterLeave = afterLeave;
        }
    }

}