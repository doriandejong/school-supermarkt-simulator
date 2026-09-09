package com.example.schoolsupermarktsimulator.objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ProduceBasket extends GameObject {

    public ProduceBasket(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(CashRegister.class.getResourceAsStream(
                        "/assets/objects/producebasket_empty.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(48);
        sprite.setPreserveRatio(true);
    }
}
