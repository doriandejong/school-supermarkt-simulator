package com.example.schoolsupermarktsimulator.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Spritesheet extends GameEntity {

    protected Image image;
    protected double spriteWidth;
    protected double spriteHeight;

    public Spritesheet(double x, double y, double spriteWidth, double spriteHeight) {
        super(x, y);

        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;

        image = new Image(
                Objects.requireNonNull(Spritesheet.class.getResourceAsStream(
                        "/assets/entities/spritesheet.png"
                ))
        );

        sprite = new ImageView(image);

        sprite.setX(x);
        sprite.setY(y);
    }

    @Override public ImageView getSprite(int index) {
        int colCount = (int)(image.getWidth() / spriteWidth); // How many next to eachother?
        int rowCount = (int)(image.getHeight() / spriteHeight); // How many below eachother?

        int column = index % rowCount;
        int row = index / colCount;

        sprite.setViewport(new Rectangle2D(column * spriteWidth, row * spriteHeight, spriteWidth, spriteHeight));

        sprite.setFitHeight(48);
        sprite.setPreserveRatio(true);
        return sprite;
    }
}
