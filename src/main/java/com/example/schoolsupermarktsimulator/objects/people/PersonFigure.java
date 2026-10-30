package com.example.schoolsupermarktsimulator.objects.people;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Eenvoudig mensfiguur: een gekleurd hoofd, een romp, armen en benen.
 * Het midden van de romp ligt op de loopknoop.
 */
public class PersonFigure extends Group {

	private final Circle head;
	private final Rectangle torso;
	private final Rectangle leftArm;
	private final Rectangle rightArm;

	public PersonFigure() {
		head = new Circle(0, -16, 9);
		head.setStroke(Color.rgb(0, 0, 0, 0.45));
		head.setStrokeWidth(1);

		torso = new Rectangle(-8, -6, 16, 16);
		torso.setArcWidth(6);
		torso.setArcHeight(6);

		leftArm = new Rectangle(-13, -4, 5, 12);
		leftArm.setArcWidth(4);
		leftArm.setArcHeight(4);
		rightArm = new Rectangle(8, -4, 5, 12);
		rightArm.setArcWidth(4);
		rightArm.setArcHeight(4);

		Rectangle leftLeg = new Rectangle(-7, 10, 5, 12);
		leftLeg.setArcWidth(3);
		leftLeg.setArcHeight(3);
		leftLeg.setFill(Color.web("#3d4450"));
		Rectangle rightLeg = new Rectangle(2, 10, 5, 12);
		rightLeg.setArcWidth(3);
		rightLeg.setArcHeight(3);
		rightLeg.setFill(Color.web("#3d4450"));

		getChildren().addAll(leftLeg, rightLeg, torso, leftArm, rightArm, head);
		setPickOnBounds(true);
		setFill(Color.RED);
	}

	/**
	 * Kleurt het hoofd en de kleding.
	 *
	 * @param color rolkleur, bijvoorbeeld rood voor een klant
	 */
	public void setFill(Color color) {
		Color shirt = color == null ? Color.RED : color;
		head.setFill(shirt);
		Color cloth = shirt.darker();
		torso.setFill(cloth);
		leftArm.setFill(cloth);
		rightArm.setFill(cloth);
	}

	/** @return de x van het midden van dit figuur */
	public double getCenterX() {
		return getLayoutX();
	}

	/** @return de y van het midden van dit figuur */
	public double getCenterY() {
		return getLayoutY();
	}

	/**
	 * @param x horizontale plek van het midden
	 */
	public void setCenterX(double x) {
		setLayoutX(x);
	}

	/**
	 * @param y verticale plek van het midden
	 */
	public void setCenterY(double y) {
		setLayoutY(y);
	}
}
