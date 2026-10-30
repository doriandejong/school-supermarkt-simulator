package com.example.schoolsupermarktsimulator.objects.warehouse;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BoxStack {

    private static final String[] TEXTURES = {
            "/assets/objects/box_brown.png",
            "/assets/objects/box_red.png",
            "/assets/objects/box_yellow.png",
            "/assets/objects/box_green.png"
    };

    /** Genoeg sprites voor een levering: elk doosje heeft twee artikelen. */
    private static final int MAX_BOXES = 16;

    private final List<ImageView> boxes = new ArrayList<>();
    private final Set<ImageView> inHand = new HashSet<>();
    private final double originX;
    private final double originY;

    public BoxStack(double x, double y) {
        this.originX = x;
        this.originY = y;
        for (int i = 0; i < MAX_BOXES; i++) {
            Image image = new Image(
                    Objects.requireNonNull(BoxStack.class.getResourceAsStream(TEXTURES[i % TEXTURES.length]))
            );
            ImageView box = new ImageView(image);
            box.setX(x);
            box.setY(y - i * 14);
            box.setFitWidth(52);
            box.setPreserveRatio(true);
            box.setMouseTransparent(true);
            box.setVisible(false);
            boxes.add(box);
        }
    }

    public List<ImageView> getSprites() {
        return boxes;
    }

    /**
     * Zet precies {@code count} dozen op de stapel bij de laadklep.
     * Elke doos hoort bij twee artikelen die de magazijnmedewerker naar een schap brengt.
     *
     * @param count aantal dozen dat de vrachtwagen heeft neergezet
     */
    public void show(int count) {
        inHand.clear();
        int visible = Math.max(0, Math.min(count, boxes.size()));
        for (int i = 0; i < boxes.size(); i++) {
            ImageView box = boxes.get(i);
            boolean onStack = i < visible;
            box.setVisible(onStack);
            if (onStack) {
                box.setX(originX);
                box.setY(originY - i * 14);
            }
        }
    }

    /**
     * Pakt de bovenste doos die nog op de stapel ligt, niet een doos die iemand al vastheeft.
     * Twee medewerkers krijgen zo ieder een eigen doos.
     *
     * @return de bovenste vrije doos, of null als de stapel leeg is
     */
    public ImageView detachTopBox() {
        for (int i = boxes.size() - 1; i >= 0; i--) {
            ImageView box = boxes.get(i);
            if (box.isVisible() && inHand.add(box)) {
                return box;
            }
        }
        return null;
    }

    /**
     * De doos is op het schap gezet en hoort niet meer bij een medewerker.
     *
     * @param box de doos die net is neergezet
     */
    public void release(ImageView box) {
        if (box != null) {
            inHand.remove(box);
        }
    }

    /** Verbergt de bovenste doos die nog op de stapel ligt, niet een doos in iemands handen. */
    public void hideOne() {
        for (int i = boxes.size() - 1; i >= 0; i--) {
            ImageView box = boxes.get(i);
            if (box.isVisible() && !inHand.contains(box)) {
                box.setVisible(false);
                break;
            }
        }
    }

    /** Haalt de hele stapel weg. Wordt aangeroepen als elke doos op een schap staat. */
    public void collect() {
        inHand.clear();
        for (ImageView box : boxes) {
            box.setVisible(false);
        }
    }
}
