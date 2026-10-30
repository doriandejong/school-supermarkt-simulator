package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class RegularProduct extends Product {

    public enum Kind {
        SNACKS("Snacks", "/assets/objects/shelf_snacks.png", 1.49),
        EGGS("Eieren", "/assets/objects/shelf_eggs.png", 2.29),
        PASTA("Pasta", "/assets/objects/shelf_pasta.png", 1.19),
        CANDY("Snoep", "/assets/objects/shelf_candy.png", 0.99),
        JAM("Jam", "/assets/objects/shelf_jam.png", 2.49);

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

    public RegularProduct(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), kind.getPrice());
        this.kind = kind;
    }

    public RegularProduct(String name) {
        super(name);
        this.kind = null;
    }

    public RegularProduct(String id, String name, double price) {
        super(id, name, price);
        this.kind = null;
    }

    public Kind getKind() {
        return kind;
    }
}
