package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class FrozenProduct extends Product {

    public enum Kind {
        COLA("Cola", "/assets/objects/drinkcooler_cola.png"),
        JUICE("Sap", "/assets/objects/drinkcooler_juice.png"),
        SODA("Frisdrank", "/assets/objects/drinkcooler_soda.png");

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

    public FrozenProduct(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), 0);
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
