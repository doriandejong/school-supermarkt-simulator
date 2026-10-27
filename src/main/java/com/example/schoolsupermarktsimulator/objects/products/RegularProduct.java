package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class RegularProduct extends Product {

    public enum Kind {
        SNACKS("Snacks", "/assets/objects/shelf_snacks.png"),
        EGGS("Eieren", "/assets/objects/shelf_eggs.png"),
        PASTA("Pasta", "/assets/objects/shelf_pasta.png"),
        CANDY("Snoep", "/assets/objects/shelf_candy.png"),
        JAM("Jam", "/assets/objects/shelf_jam.png");

        private final String displayName;
        private final String texture;

        Kind(String displayName, String texture) {
            this.displayName = displayName;
            this.texture = texture;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTexture() {
            return texture;
        }

        public static Kind random() {
            Kind[] kinds = values();
            return kinds[ThreadLocalRandom.current().nextInt(kinds.length)];
        }
    }

    private final Kind kind;

    public RegularProduct(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), 0);
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
