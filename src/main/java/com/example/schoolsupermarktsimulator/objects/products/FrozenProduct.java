package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class FrozenProduct extends Product {

    public enum Kind {
        COLA("Cola", "/assets/objects/drinkcooler_cola.png", 1.59),
        JUICE("Sap", "/assets/objects/drinkcooler_juice.png", 1.79),
        SODA("Frisdrank", "/assets/objects/drinkcooler_soda.png", 1.69);

        private final String displayName;
        private final String texture;
        private final double price;

        Kind(String displayName, String texture, double price) {
            this.displayName = displayName;
            this.texture = texture;
            this.price = price;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTexture() {
            return texture;
        }

        public double getPrice() {
            return price;
        }

        public static Kind random() {
            Kind[] kinds = values();
            return kinds[ThreadLocalRandom.current().nextInt(kinds.length)];
        }
    }

    private final Kind kind;

    public FrozenProduct(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), kind.getPrice());
        this.kind = kind;
    }

    public FrozenProduct(String name) {
        super(name);
        this.kind = null;
    }

    public FrozenProduct(String id, String name, double price) {
        super(id, name, price);
        this.kind = null;
    }

    public Kind getKind() {
        return kind;
    }
}
