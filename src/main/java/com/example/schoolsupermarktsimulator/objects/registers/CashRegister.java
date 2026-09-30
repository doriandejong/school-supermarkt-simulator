package com.example.schoolsupermarktsimulator.objects.registers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class CashRegister extends Register {

    public CashRegister(double x, double y) {
        super(x, y);

        Image image = new Image(
                Objects.requireNonNull(CashRegister.class.getResourceAsStream(
                        "/assets/objects/cash_register.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);

        sprite.setFitWidth(192);
        sprite.setPreserveRatio(true);
    }
}
