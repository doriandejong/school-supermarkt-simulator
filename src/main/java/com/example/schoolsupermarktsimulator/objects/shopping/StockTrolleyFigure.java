package com.example.schoolsupermarktsimulator.objects.shopping;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Tekening van een vulkar: een laag plateau, een krat en een duwstang.
 * Wijkt expres af van de winkelwagen, zodat de kar in het magazijn herkenbaar is.
 */
public class StockTrolleyFigure extends Group {

	public StockTrolleyFigure() {
		Rectangle bed = new Rectangle(-20, 0, 40, 8);
		bed.setArcWidth(3);
		bed.setArcHeight(3);
		bed.setFill(Color.web("#d9782d"));
		bed.setStroke(Color.web("#6b3a12"));

		Rectangle crate = new Rectangle(-10, -14, 20, 14);
		crate.setFill(Color.web("#c4a574"));
		crate.setStroke(Color.web("#6b5128"));

		Line handle = new Line(18, 2, 18, -20);
		handle.setStroke(Color.web("#3a3f46"));
		handle.setStrokeWidth(2.4);
		Line grip = new Line(18, -20, 8, -20);
		grip.setStroke(Color.web("#3a3f46"));
		grip.setStrokeWidth(2.4);

		Circle leftWheel = new Circle(-12, 12, 4, Color.web("#22262c"));
		Circle rightWheel = new Circle(12, 12, 4, Color.web("#22262c"));

		getChildren().addAll(handle, grip, bed, crate, leftWheel, rightWheel);
		setMouseTransparent(true);
	}

	/**
	 * Zet het midden van de kar op deze plek.
	 *
	 * @param x horizontale plek
	 * @param y verticale plek
	 */
	public void setCenter(double x, double y) {
		setLayoutX(x);
		setLayoutY(y);
	}
}
