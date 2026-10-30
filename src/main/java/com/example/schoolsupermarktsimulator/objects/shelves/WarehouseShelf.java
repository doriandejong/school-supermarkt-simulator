package com.example.schoolsupermarktsimulator.objects.shelves;

import com.example.schoolsupermarktsimulator.objects.products.Product;
import com.example.schoolsupermarktsimulator.objects.warehouse.Warehouse;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarehouseShelf extends Shelf {

    public static final int CAPACITY = 15;
    public static final int MIN_STOCK_FOR_TRUCK = 5;

    // Doosjes op warehouse_shelf.png, van de onderste plank naar boven.
    // x, y, breedte, hoogte in pixels van de texture (256x189).
    private static final int[][] SLOTS = {
            {8, 131, 55, 26},
            {70, 131, 54, 26},
            {132, 131, 55, 26},
            {194, 131, 55, 26},
            {8, 96, 55, 29},
            {70, 96, 55, 29},
            {132, 96, 55, 29},
            {8, 61, 55, 26},
            {70, 61, 55, 26},
            {132, 61, 55, 27},
            {194, 61, 55, 26},
            {8, 0, 56, 53},
            {70, 0, 56, 54},
            {132, 0, 56, 53},
            {194, 0, 56, 53}
    };

    private static final Image STOCK = new Image(
            Objects.requireNonNull(WarehouseShelf.class.getResourceAsStream(
                    "/assets/objects/warehouse_shelf.png"
            ))
    );

    private final List<ImageView> slots = new ArrayList<>();

    public WarehouseShelf(double x, double y, String shelfId, Warehouse warehouse) {
        super(x, y, shelfId, CAPACITY, 0, warehouse);

        Image image = new Image(
                Objects.requireNonNull(WarehouseShelf.class.getResourceAsStream(
                        "/assets/objects/shelf_empty.png"
                ))
        );

        sprite = new ImageView(image);
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(128);
        sprite.setPreserveRatio(true);

        double scale = sprite.getFitWidth() / image.getWidth();
        for (int[] slot : SLOTS) {
            ImageView box = new ImageView(STOCK);
            box.setViewport(new Rectangle2D(slot[0], slot[1], slot[2], slot[3]));
            box.setFitWidth(slot[2] * scale);
            box.setPreserveRatio(true);
            box.setX(x + slot[0] * scale);
            box.setY(y + slot[1] * scale);
            box.setMouseTransparent(true);
            box.setVisible(false);
            slots.add(box);
        }

        setupTooltip();
        refreshStockVisual();
    }

    public List<ImageView> getStockSlots() {
        return slots;
    }

    /**
     * Een magazijnschap legt verschillende artikelen door elkaar.
     * Daarom is er geen vast artikel, en accepteert {@link Shelf#accepts(Product)} alles.
     *
     * @return null
     */
    @Override
    public Product createStockItem() {
        return null;
    }

    /**
     * Houdt geen prototype vast, anders zou de eerste doos het schap op één artikel vastzetten.
     *
     * @param product artikel uit een doos; wordt genegeerd
     */
    @Override
    protected void rememberStockedProduct(Product product) {
        // Magazijnschappen blijven gemengd.
    }

    /**
     * Vult dit schap in één keer met anonieme magazijnproducten.
     * De normale route is {@code WarehouseController}: die brengt dozen één voor één van de vrachtwagen.
     */
    public void restockFromTruck() {
        int missing = getCapacity() - getCurrentStockCount();
        List<Product> delivery = new ArrayList<>();
        for (int i = 0; i < missing; i++) {
            delivery.add(new Product("Magazijnproduct"));
        }
        fillShelf(delivery);
    }

    @Override
    public int fillShelf(List<Product> newProducts) {
        int added = super.fillShelf(newProducts);
        refreshStockVisual();
        return added;
    }

    @Override
    public Product takeProduct(Product product) {
        Product taken = super.takeProduct(product);
        refreshStockVisual();
        return taken;
    }

    private void refreshStockVisual() {
        int stock = getCurrentStockCount();
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setVisible(i < stock);
        }
    }
}
