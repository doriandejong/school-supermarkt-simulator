package com.example.schoolsupermarktsimulator.objects.warehouse;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoxStack {

    private static final String[] TEXTURES = {
            "/assets/objects/box_brown.png",
            "/assets/objects/box_red.png",
            "/assets/objects/box_yellow.png",
            "/assets/objects/box_green.png"
    };

    private final List<ImageView> boxes = new ArrayList<>();
    private boolean waitingToBeShelved;

    public BoxStack(double x, double y) {
        for (int i = 0; i < TEXTURES.length; i++) {
            Image image = new Image(
                    Objects.requireNonNull(BoxStack.class.getResourceAsStream(TEXTURES[i]))
            );
            ImageView box = new ImageView(image);
            box.setX(x);
            box.setY(y - i * 18);
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

    public void show() {
        waitingToBeShelved = true;
        for (ImageView box : boxes) {
            box.setVisible(true);
        }
    }

    public boolean isWaitingToBeShelved() {
        return waitingToBeShelved;
    }

    public void collect() {
        waitingToBeShelved = false;
        for (ImageView box : boxes) {
            box.setVisible(false);
        }
    }
}
