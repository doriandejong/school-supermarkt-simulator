package com.example.schoolsupermarktsimulator.objects.shopping;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

/**
 * Tekening van een winkelwagen: mand, duwbeugel en twee wielen.
 */
public class ShoppingCartFigure extends Group {

	public ShoppingCartFigure() {
		Polygon basket = new Polygon(
				-16, -6,
				16, -6,
				12, 10,
				-12, 10
		);
		basket.setFill(Color.rgb(186, 198, 210, 0.92));
		basket.setStroke(Color.web("#3e4a56"));
		basket.setStrokeWidth(1.4);

		Line wireTop = new Line(-15, -1, 15, -1);
		Line wireMid = new Line(-14, 4, 14, 4);
		wireTop.setStroke(Color.web("#3e4a56"));
		wireMid.setStroke(Color.web("#3e4a56"));

		Polyline handle = new Polyline(
				-14, -4,
				-14, -18,
				2, -18
		);
		handle.setStroke(Color.web("#2c333c"));
		handle.setStrokeWidth(2);
		handle.setFill(null);

		Circle leftWheel = new Circle(-8, 14, 3.5, Color.web("#22262c"));
		Circle rightWheel = new Circle(8, 14, 3.5, Color.web("#22262c"));

		getChildren().addAll(handle, basket, wireTop, wireMid, leftWheel, rightWheel);
		setMouseTransparent(true);
	}

	/**
	 * Zet het midden van de wagen op deze plek.
	 *
	 * @param x horizontale plek
	 * @param y verticale plek
	 */
	public void setCenter(double x, double y) {
		setLayoutX(x);
		setLayoutY(y);
	}
}
