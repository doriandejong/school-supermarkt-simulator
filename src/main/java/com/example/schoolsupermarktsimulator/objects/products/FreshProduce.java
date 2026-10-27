package com.example.schoolsupermarktsimulator.objects.products;

import java.util.concurrent.ThreadLocalRandom;

public class FreshProduce extends Product {

    public enum Kind {
        MELON("Meloen", "/assets/objects/producebin_melon.png"),
        CABBAGE("Kool", "/assets/objects/producebin_cabbage.png"),
        PUMPKIN("Pompoen", "/assets/objects/producebin_pumpkin.png");

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

    public FreshProduce(Kind kind) {
        super(kind.name().toLowerCase(), kind.getDisplayName(), 0);
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
