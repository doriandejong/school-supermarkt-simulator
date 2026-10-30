package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.products.RegularProduct;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class RegularShelf extends Shelf {

    // De texture is breder dan het schap; dit is het schap zelf.
    private static final Rectangle2D SHELF_VIEWPORT = new Rectangle2D(164, 0, 696, 512);

    private static final Image EMPTY = new Image(
            Objects.requireNonNull(RegularShelf.class.getResourceAsStream(
                    "/assets/objects/shelf_empty.png"
            ))
    );

    private final RegularProduct.Kind kind;
    private final Image filled;

    public RegularShelf(double x, double y, Warehouse warehouse) {
        this(x, y, warehouse, RegularProduct.Kind.random());
    }

    public RegularShelf(double x, double y, Warehouse warehouse, RegularProduct.Kind kind) {
        // Geef x, y, id, capaciteit (bijv. 20) en drempelwaarde (bijv. 5) mee
        super(x, y, "REGULAR-" + kind.name(), 20, 5, warehouse);
        this.kind = kind;
        this.filled = new Image(
                Objects.requireNonNull(RegularShelf.class.getResourceAsStream(kind.getTexture()))
        );

        sprite = new ImageView();
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(128);
        sprite.setPreserveRatio(true);
        updateTexture();

        setupTooltip();
    }

    public RegularProduct.Kind getKind() {
        return kind;
    }

    /**
     * Een nieuw exemplaar van het vaste soort op dit schap, inclusief de prijs.
     *
     * @return een {@link RegularProduct} van dit schap
     */
    @Override
    public Product createStockItem() {
        return new RegularProduct(kind);
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
            sprite.setViewport(SHELF_VIEWPORT);
        }
    }
}
