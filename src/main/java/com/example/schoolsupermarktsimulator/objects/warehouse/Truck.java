package com.example.schoolsupermarktsimulator.objects.warehouse;

import com.example.schoolsupermarktsimulator.objects.GameObject;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class Truck extends GameObject {

    private static final double DRIVE_SECONDS = 1.5;
    private static final double COOLDOWN_SECONDS = 5;

    private boolean delivering;
    private boolean orderedAgain;
    private Runnable onArrived;
    private BooleanSupplier canDeliver;

    public Truck(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(Truck.class.getResourceAsStream(
                        "/assets/entities/truck.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitHeight(240);
        sprite.setPreserveRatio(true);
        sprite.setMouseTransparent(true);
        hideOffscreen();
    }

    public void deliver(BooleanSupplier canDeliver, Runnable onArrived) {
        this.canDeliver = canDeliver;
        this.onArrived = onArrived;
        if (canDeliver != null && !canDeliver.getAsBoolean()) {
            return;
        }
        if (delivering) {
            orderedAgain = true;
            return;
        }
        delivering = true;
        playTrip();
    }

    private void playTrip() {
        double offscreen = offscreenShift();
        sprite.setTranslateX(offscreen);
        sprite.setVisible(true);
        sprite.toFront();

        TranslateTransition driveIn = new TranslateTransition(Duration.seconds(DRIVE_SECONDS), sprite);
        driveIn.setFromX(offscreen);
        driveIn.setToX(0);
        driveIn.setOnFinished(event -> {
            if (onArrived != null) {
                onArrived.run();
            }
        });

        PauseTransition cooldown = new PauseTransition(Duration.seconds(COOLDOWN_SECONDS));

        TranslateTransition driveOut = new TranslateTransition(Duration.seconds(DRIVE_SECONDS), sprite);
        driveOut.setFromX(0);
        driveOut.setToX(offscreen);

        SequentialTransition trip = new SequentialTransition(driveIn, cooldown, driveOut);
        trip.setOnFinished(event -> finishTrip());
        trip.play();
    }

    private void finishTrip() {
        hideOffscreen();
        delivering = false;
        if (orderedAgain) {
            orderedAgain = false;
            if (canDeliver != null && !canDeliver.getAsBoolean()) {
                return;
            }
            delivering = true;
            playTrip();
        }
    }

    private void hideOffscreen() {
        sprite.setVisible(false);
        sprite.setTranslateX(offscreenShift());
    }

    private double offscreenShift() {
        double imageHeight = sprite.getImage().getHeight();
        double shownWidth = imageHeight > 0
                ? sprite.getFitHeight() * sprite.getImage().getWidth() / imageHeight
                : 320;
        double rightEdge = x + shownWidth;
        return -(rightEdge + 40);
    }
}
