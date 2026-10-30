package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.FrozenProduct;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class Freezer extends Shelf {

    private static final Image EMPTY = new Image(
            Objects.requireNonNull(Freezer.class.getResourceAsStream(
                    "/assets/objects/drinkcooler_empty.png"
            ))
    );

    private final FrozenProduct.Kind kind;
    private final Image filled;

    public Freezer(double x, double y, Warehouse warehouse) {
        this(x, y, warehouse, FrozenProduct.Kind.random());
    }

    public Freezer(double x, double y, Warehouse warehouse, FrozenProduct.Kind kind) {
        super(x, y, "FREEZER-" + kind.name(), 18, 4, warehouse);
        this.kind = kind;
        this.filled = new Image(
                Objects.requireNonNull(Freezer.class.getResourceAsStream(kind.getTexture()))
        );

        sprite = new ImageView();
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(48);
        sprite.setPreserveRatio(true);
        updateTexture();

        setupTooltip();
    }

    public FrozenProduct.Kind getKind() {
        return kind;
    }

    @Override
    public int fillShelf(List<Product> newProducts) {
        int added = super.fillShelf(newProducts);
        updateTexture();
        return added;
    }

    @Override
    public Product takeProduct(Product product) {
        Product taken = super.takeProduct(product);
        updateTexture();
        return taken;
    }

    private void updateTexture() {
        sprite.setImage(getCurrentStockCount() == 0 ? EMPTY : filled);
    }
}
