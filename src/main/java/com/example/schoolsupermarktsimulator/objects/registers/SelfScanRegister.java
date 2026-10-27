package com.example.schoolsupermarktsimulator.objects.registers;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SelfScanRegister extends Register {

	private static final Rectangle2D REGISTER_VIEWPORT = new Rectangle2D(116, 74, 792, 489);

	public SelfScanRegister(double x, double y) {
		super(x, y);

		Image image = new Image(
				Objects.requireNonNull(SelfScanRegister.class.getResourceAsStream(
						"/assets/objects/selfscan.png"
				))
		);

		sprite = new ImageView(image);
		sprite.setViewport(REGISTER_VIEWPORT);
		sprite.setX(x);
		sprite.setY(y);
		sprite.setFitWidth(160);
		sprite.setPreserveRatio(true);
	}

	public boolean doRandomInspection() {
		return false;
	}

}
