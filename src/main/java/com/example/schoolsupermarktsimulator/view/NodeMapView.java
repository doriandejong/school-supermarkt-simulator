package com.example.schoolsupermarktsimulator.view;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tekent de loopknooppunten en de paden daartussen over de winkelvloer.
 * De laag vangt geen muisklikken, zodat schappen en personen gewoon aanklikbaar blijven.
 */
public final class NodeMapView {

	private static final Color PATH = Color.web("#1d4ed8", 0.72);
	private static final Color DOT = Color.web("#ffffff", 0.92);
	private static final Color DOT_EDGE = Color.web("#1d4ed8");
	private static final Color LABEL = Color.web("#123a73");

	private NodeMapView() {
	}

	/**
	 * @param nodes alle knooppunten van de plattegrond
	 * @return een laag met lijnen, bolletjes en het knooppuntnummer
	 */
	public static Pane create(List<Node> nodes) {
		Pane overlay = new Pane();
		overlay.setMouseTransparent(true);
		overlay.setPickOnBounds(false);
		if (nodes == null) {
			return overlay;
		}

		Set<String> drawn = new HashSet<>();
		for (Node node : nodes) {
			for (Node neighbor : node.getNeighbors()) {
				int low = Math.min(node.getId(), neighbor.getId());
				int high = Math.max(node.getId(), neighbor.getId());
				if (!drawn.add(low + "-" + high)) {
					continue;
				}
				Line path = new Line(node.getX(), node.getY(), neighbor.getX(), neighbor.getY());
				path.setStroke(PATH);
				path.setStrokeWidth(2);
				overlay.getChildren().add(path);
			}
		}

		for (Node node : nodes) {
			Circle dot = new Circle(node.getX(), node.getY(), 8, DOT);
			dot.setStroke(DOT_EDGE);
			dot.setStrokeWidth(1.5);

			Text label = new Text(node.getIdAsString());
			label.setFont(Font.font("SansSerif", FontWeight.BOLD, 9));
			label.setFill(LABEL);
			double width = label.getLayoutBounds().getWidth();
			double height = label.getLayoutBounds().getHeight();
			label.setX(node.getX() - width / 2);
			label.setY(node.getY() + height / 4);

			overlay.getChildren().addAll(dot, label);
		}
		return overlay;
	}
}
