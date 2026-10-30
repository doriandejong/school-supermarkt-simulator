package com.example.schoolsupermarktsimulator.objects.registers;

import com.example.schoolsupermarktsimulator.objects.people.Cashier;
import com.example.schoolsupermarktsimulator.objects.people.Customer;
import com.example.schoolsupermarktsimulator.objects.people.Employee;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Gewone kassa. Een klant kan hier alleen betalen als er een kassamedewerker aan gekoppeld is.
 * Die medewerker scant elk artikel, daarna betaalt de klant het totaal.
 */
public class CashRegister extends Register {

    private Cashier cashier;

    public CashRegister(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(CashRegister.class.getResourceAsStream(
                        "/assets/objects/cash_register.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(192);
        sprite.setPreserveRatio(true);
    }

    /** @return de kassamedewerker die hier scant, of null als de kassa onbemand is */
    public Cashier getCashier() {
        return cashier;
    }

    /**
     * Zet de kassamedewerker achter deze kassa. Zonder medewerker lukt afrekenen niet.
     *
     * @param cashier de medewerker die de artikelen scant
     */
    public void assignCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    /**
     * Laat de gekoppelde kassamedewerker alles scannen en de klant betalen.
     * De parameter inspector wordt niet gebruikt; de gewone kassa heeft zijn eigen medewerker.
     *
     * @param customer klant die wil afrekenen
     * @param inspector wordt genegeerd
     * @return true als de betaling gelukt is
     */
    @Override
    public boolean completeCheckout(Customer customer, Employee inspector) {
        if (customer == null || cashier == null || !isOpen()) {
            return false;
        }
        if (!proceedToRegister(customer)) {
            return false;
        }
        for (Product product : productsOf(customer)) {
            cashier.scan(product);
        }
        // Het totaal staat op de klant via de prijzen in de mand. Betalen maakt de mand leeg.
        boolean paid = customer.payGroceries();
        finishCurrentCustomer();
        return paid;
    }
}
