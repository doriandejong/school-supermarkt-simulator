package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class FreshProduce extends Product {

    public enum Kind {
        MELON("Meloen", "/assets/objects/producebin_melon.png", 2.50),
        CABBAGE("Kool", "/assets/objects/producebin_cabbage.png", 1.80),
        PUMPKIN("Pompoen", "/assets/objects/producebin_pumpkin.png", 3.00);

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

    public FreshProduce(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), kind.getPrice());
        this.kind = kind;
    }

    public FreshProduce(String name) {
        super(name);
        this.kind = null;
    }

    public FreshProduce(String id, String name, double price) {
        super(id, name, price);
        this.kind = null;
    }

    public Kind getKind() {
        return kind;
    }
}
