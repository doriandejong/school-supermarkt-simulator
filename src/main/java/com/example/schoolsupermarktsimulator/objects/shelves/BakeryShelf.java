package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class BakeryShelf extends Shelf {

    public BakeryShelf(double x, double y, Warehouse warehouse) {
        super(x, y, "REGULAR-1", 40, 10, warehouse);

        Image image = new Image(
                Objects.requireNonNull(BakeryShelf.class.getResourceAsStream(
                        "/assets/objects/bakery.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(240);
        sprite.setPreserveRatio(true);

        setupTooltip();
    }

    /**
     * De bakkerij verkoopt brood. Elk nieuw exemplaar heeft dezelfde naam en prijs.
     *
     * @return een brood van 1,25 euro
     */
    @Override
    public Product createStockItem() {
        return new Product("brood", "Brood", 1.25);
    }
}
