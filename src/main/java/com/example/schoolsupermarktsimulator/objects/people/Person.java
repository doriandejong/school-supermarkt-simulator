package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.GameEntity;
import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.warehouse.Truck;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class Person{

	public Circle sprite;

	private final int id;

	private final String name;

	private final double xPos;

	private final double yPos;
	private List<Node> path;
	private int targetIndex = 0; // De node waar de persoon heen loopt
	private double speed = 64; // Pixels per seconde

	private Tooltip tooltip;

	public Person(double xPos, double yPos, int id, String name) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.id = id;
		this.name = name;

		this.sprite = new Circle(xPos, yPos, 12);
		sprite.setFill(Color.RED);

		setupTooltip();
	}

	public Person(double xPos, double yPos, int id, String name, double speed) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.id = id;
		this.name = name;
		this.speed = speed;

		this.sprite = new Circle(xPos, yPos, 12);
		sprite.setFill(Color.RED);

		setupTooltip();
	}

	public void setPath(List<Node> newPath) {
		System.out.println(hasFinishedPath());
		this.path = newPath;
		this.targetIndex = 1; // Skip de eerste node, daar staat de persoon al

		if (!path.isEmpty()) {
			sprite.setCenterX(path.getFirst().getX());
			sprite.setCenterY(path.getFirst().getY());
		}
	}

	public void startMoving() {
		new AnimationTimer() {
			private long previousTime = 0;

			@Override public void handle(long now) {
				if (previousTime == 0) {
					previousTime = now;
					return;
				}

				double dt = (now - previousTime) / 1_000_000_000.0;
				previousTime = now;

				move(dt);
			}
		}.start();
	}

	private void move(double deltaTime) {
		if (path == null || targetIndex >= path.size()) {
			return;
		}

		Node target = path.get(targetIndex);

		double dx = target.getX() - sprite.getCenterX();
		double dy = target.getY() - sprite.getCenterY();
		double distance = Math.hypot(dx, dy);

		double step = speed * deltaTime;

		// Als persoon minder ver hoeft te lopen dan zij zou kunnen lopen, direct naar target gaan
		if (distance <= step || distance == 0) {
			sprite.setCenterX(target.getX());
			sprite.setCenterY(target.getY());
			targetIndex++;
		// Als persoon verder moet lopen, loop zover als je binnen de dt kunt en stop daar
		} else {
			sprite.setCenterX(
					sprite.getCenterX() + dx / distance * step
			);
			sprite.setCenterY(
					sprite.getCenterY() + dy / distance * step
			);
		}
	}

	public boolean hasFinishedPath() {
		return path != null && targetIndex >= path.size();
	}

	//	Koppelt de tooltip aan de JavaFX sprite van dit schap.
	//	Moet worden aangeroepen zodra de 'sprite' (ImageView) in de subklasse is aangemaakt.
	protected void setupTooltip() {
		if (sprite != null) {
			tooltip = new Tooltip();
			// Optioneel: zorg dat de tooltip direct verschijnt zonder vertraging
			tooltip.setShowDelay(Duration.millis(100));
			// Koppel de tooltip aan de sprite
			Tooltip.install(sprite, tooltip);
			// Zet de initiële tekst (bijv. "0/40")
			updateTooltipText();
		}
	}

	//	Werkt de tekst van de tooltip bij naar het huidige aantal / maximale capaciteit.
	private void updateTooltipText() {
		if (tooltip != null) {
			tooltip.setText(name + " (" + id + ")");
		}
	}

}
