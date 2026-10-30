package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.FreshProduce;
import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class VegetableShelf extends Shelf {

    // De gevulde texture is vierkant met lege randen; dit is het bakje zelf.
    private static final Rectangle2D BIN_VIEWPORT = new Rectangle2D(188, 22, 651, 981);

    private static final Image EMPTY = new Image(
            Objects.requireNonNull(VegetableShelf.class.getResourceAsStream(
                    "/assets/objects/producebin_empty.png"
            ))
    );

    private final FreshProduce.Kind kind;
    private final Image filled;

    public VegetableShelf(double x, double y, Warehouse warehouse) {
        this(x, y, warehouse, FreshProduce.Kind.random());
    }

    public VegetableShelf(double x, double y, Warehouse warehouse, FreshProduce.Kind kind) {
        super(x, y, "VEGETABLE-" + kind.name(), 7, 2, warehouse);
        this.kind = kind;
        this.filled = new Image(
                Objects.requireNonNull(VegetableShelf.class.getResourceAsStream(kind.getTexture()))
        );

        sprite = new ImageView();
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(64);
        sprite.setPreserveRatio(true);
        updateTexture();

        setupTooltip();
    }

    public FreshProduce.Kind getKind() {
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
        if (getCurrentStockCount() == 0) {
            sprite.setImage(EMPTY);
            sprite.setViewport(null);
        } else {
            sprite.setImage(filled);
            sprite.setViewport(BIN_VIEWPORT);
        }
    }
}
