package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import com.example.schoolsupermarktsimulator.objects.nodes.Pathfinder;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Iemand die door de winkel loopt. Klanten en medewerkers zijn allebei een persoon;
 * zij verschillen in wat ze doen als ze ergens aankomen.
 */
public class Person {

	public PersonFigure sprite;
	private final int id;
	private final String name;
	private List<Node> path;
	private final Queue<Node> walkingRoute = new LinkedList<>();
	public Node currentNode;
	private Node currentDestination;
	private int targetIndex = 0;
	private double speed = 64;
	private Tooltip tooltip;
	private AnimationTimer mover;
	private Consumer<Node> onArrived;
	private Runnable onRouteFinished;
	private Runnable onMoved;
	private PauseTransition dwellPause;
	private boolean dwelling;
	private double pendingDwellSeconds;
	private Runnable pendingDwellAction;

	/**
	 * Zet een persoon op een knoop van de looproute.
	 *
	 * @param startNode de knoop waar de persoon begint
	 * @param id nummer waarmee deze persoon herkenbaar is
	 * @param name naam die in de tooltip staat
	 */
	public Person(Node startNode, int id, String name) {
		this.currentNode = startNode;
		this.id = id;
		this.name = name;

		createSprite();
	}

	/**
	 * Zet een persoon op een knoop en kiest hoe snel die loopt.
	 *
	 * @param startNode de knoop waar de persoon begint
	 * @param id nummer waarmee deze persoon herkenbaar is
	 * @param name naam die in de tooltip staat
	 * @param speed loopsnelheid in pixels per seconde
	 */
	public Person(Node startNode, int id, String name, double speed) {
		this.currentNode = startNode;
		this.id = id;
		this.name = name;
		this.speed = speed;

		createSprite();
	}

	/**
	 * Bouwt het figuur op de startknoop en zet de hovertekst met de rol.
	 */
	private void createSprite() {
		this.sprite = new PersonFigure();
		sprite.setFill(Color.RED);
		sprite.setCenterX(currentNode.getX());
		sprite.setCenterY(currentNode.getY());
		setupTooltip();
	}

	/** @return het nummer van deze persoon */
	public int getId() {
		return id;
	}

	/** @return de naam van deze persoon */
	public String getName() {
		return name;
	}

	/**
	 * Rol die bij hover getoond wordt. Subklassen geven hun eigen functie terug.
	 *
	 * @return bijvoorbeeld Klant of Kassamedewerker
	 */
	public String roleName() {
		return "Persoon";
	}

	/** @return rol en naam, zoals "Kassamedewerker: Sara" */
	public String getRoleLabel() {
		return roleName() + ": " + name;
	}

	/**
	 * Kleurt hoofd en kleding.
	 *
	 * @param color rolkleur
	 */
	public void setShirt(Color color) {
		sprite.setFill(color);
	}

	/**
	 * Zet het figuur op een vaste plek, los van de loopknoop.
	 * De kassamedewerker staat zo naast de stoel en niet in de rij.
	 *
	 * @param x horizontale plek
	 * @param y verticale plek
	 */
	public void setPosition(double x, double y) {
		sprite.setCenterX(x);
		sprite.setCenterY(y);
	}

	/** @return horizontale plek van het midden van het figuur */
	public double getX() {
		return sprite.getCenterX();
	}

	/** @return verticale plek van het midden van het figuur */
	public double getY() {
		return sprite.getCenterY();
	}

	/**
	 * Hangt een wagen of kar aan dit figuur, zodat die meeloopt.
	 *
	 * @param item tekening van de wagen of kar
	 * @param offsetX verschuiving naar rechts, in pixels
	 * @param offsetY verschuiving naar beneden, in pixels
	 */
	public void carry(javafx.scene.Node item, double offsetX, double offsetY) {
		item.setTranslateX(offsetX);
		item.setTranslateY(offsetY);
		item.setMouseTransparent(true);
		sprite.getChildren().add(item);
	}

	/**
	 * Wordt aangeroepen telkens als de persoon bij een tussenstop van de route is.
	 * Gebruik dit om een product te pakken of een doos neer te zetten.
	 *
	 * @param onArrived ontvangt de knoop waar de persoon net is aangekomen
	 */
	public void setOnArrived(Consumer<Node> onArrived) {
		this.onArrived = onArrived;
	}

	/**
	 * Wordt één keer aangeroepen als de hele route leeg is.
	 * Daarna wordt de callback gewist, zodat een volgende route zijn eigen einde heeft.
	 *
	 * @param onRouteFinished wat er gebeurt na de laatste stop
	 */
	public void setOnRouteFinished(Runnable onRouteFinished) {
		this.onRouteFinished = onRouteFinished;
	}

	/**
	 * Wordt aangeroepen terwijl de persoon een stap zet.
	 * Een meegedragen doos kan zo met de persoon meebewegen.
	 *
	 * @param onMoved callback zonder argument, of null om hem uit te zetten
	 */
	public void setOnMoved(Runnable onMoved) {
		this.onMoved = onMoved;
	}

	/** @return true zolang de loop-animatie nog bezig is */
	public boolean isWalking() {
		return mover != null;
	}

	/**
	 * @return true zolang deze persoon loopt of stilstaat voor een handeling
	 * zoals pakken, vullen of afrekenen
	 */
	public boolean isBusy() {
		return mover != null || dwelling;
	}

	/**
	 * Blijft op de huidige stop staan, voert daarna de handeling uit en loopt dan pas verder.
	 * Aanroepen vanuit de aankomst-callback, voordat de route naar de volgende stop gaat.
	 *
	 * @param seconds hoe lang de persoon blijft staan
	 * @param action wat er gebeurt als de tijd om is, bijvoorbeeld een artikel pakken
	 */
	public void dwellBeforeContinue(double seconds, Runnable action) {
		if (seconds > 0) {
			pendingDwellSeconds += seconds;
		}
		if (action == null) {
			return;
		}
		Runnable earlier = pendingDwellAction;
		pendingDwellAction = () -> {
			if (earlier != null) {
				earlier.run();
			}
			action.run();
		};
	}

	/**
	 * Blijft staan en voert daarna een actie uit, zonder zelf een volgende looproute te starten.
	 * Gebruik dit aan het eind van een route, bijvoorbeeld bij het afrekenen.
	 *
	 * @param seconds hoe lang de persoon blijft staan
	 * @param after wat er gebeurt als de tijd om is
	 */
	public void dwell(double seconds, Runnable after) {
		cancelDwell();
		if (seconds <= 0) {
			if (after != null) {
				after.run();
			}
			return;
		}
		dwelling = true;
		dwellPause = new PauseTransition(Duration.seconds(seconds));
		dwellPause.setOnFinished(event -> {
			dwelling = false;
			dwellPause = null;
			if (after != null) {
				after.run();
			}
		});
		dwellPause.play();
	}

	/** Stopt een lopende wachttijd. De actie aan het eind wordt dan niet uitgevoerd. */
	private void cancelDwell() {
		pendingDwellSeconds = 0;
		pendingDwellAction = null;
		dwelling = false;
		if (dwellPause != null) {
			dwellPause.stop();
			dwellPause = null;
		}
	}

	/**
	 * Vervangt de lijst met tussenstops en begint met lopen.
	 * Tussen twee stops zoekt {@link Pathfinder} zelf de kortste knopen.
	 *
	 * @param nodes de plekken die de persoon na elkaar moet bereiken
	 */
	public void setWalkingRoute(List<Node> nodes) {
		walkingRoute.clear();
		if (nodes != null) {
			for (Node node : nodes) {
				if (node != null) {
					walkingRoute.add(node);
				}
			}
		}
		System.out.println(walkingRoute.stream().map(Node::getIdAsString).collect(Collectors.joining(" - ")));
		stopMoving();
		routeToNextDestination();
	}

	/**
	 * Pakt de volgende stop uit de route en zoekt daar een pad naartoe.
	 * Is de route leeg, dan is de persoon klaar en draait de eind-callback.
	 */
	private void routeToNextDestination() {
		if (walkingRoute.isEmpty()) {
			Runnable done = onRouteFinished;
			onRouteFinished = null;
			if (done != null) {
				done.run();
			}
			return;
		}

		currentDestination = walkingRoute.poll();
		System.out.println("Pathfinding from " + currentNode.getId() + " to " + currentDestination.getId());
		List<Node> nextPath = Pathfinder.findPath(currentNode, currentDestination);
		System.out.println("Found path " + nextPath.stream().map(Node::getIdAsString).collect(Collectors.joining(" - ")));
		if (nextPath.isEmpty()) {
			routeToNextDestination();
			return;
		}
		setPath(nextPath);
		startMoving();
	}

	/**
	 * Zet het concrete pad dat de persoon nu stap voor stap aflegt.
	 * De eerste knoop is de plek waar de persoon al staat, die wordt overgeslagen.
	 *
	 * @param newPath knopen van de huidige positie tot de volgende stop
	 */
	public void setPath(List<Node> newPath) {
		this.path = newPath;
		this.targetIndex = 1;

		if (path != null && !path.isEmpty()) {
			sprite.setCenterX(path.getFirst().getX());
			sprite.setCenterY(path.getFirst().getY());
		}
	}

	/**
	 * Start de animatie die {@link #move(double)} elke frame aanroept.
	 * Er loopt hoogstens één timer tegelijk, anders zou de persoon dubbel stappen.
	 */
	public void startMoving() {
		if (mover != null) {
			return;
		}
		mover = new AnimationTimer() {
			private long previousTime = 0;

			@Override
			public void handle(long now) {
				if (previousTime == 0) {
					previousTime = now;
					return;
				}

				double dt = (now - previousTime) / 1_000_000_000.0;
				previousTime = now;

				move(dt);
				if (hasFinishedPath()) {
					stop();
					mover = null;
					onPathFinished();
				}
			}
		};
		mover.start();
	}

	/** Stopt de loop-animatie en een eventuele wachttijd, zonder de route als afgerond te zien. */
	private void stopMoving() {
		if (mover != null) {
			mover.stop();
			mover = null;
		}
		cancelDwell();
	}

	/**
	 * Zet één stap richting de volgende knoop, begrensd door de snelheid en de frametijd.
	 * Staat de persoon dicht genoeg bij de knoop, dan springt die er precies op.
	 *
	 * @param deltaTime seconden sinds de vorige frame
	 */
	private void move(double deltaTime) {
		if (path == null || targetIndex >= path.size()) {
			return;
		}

		Node target = path.get(targetIndex);

		double dx = target.getX() - sprite.getCenterX();
		double dy = target.getY() - sprite.getCenterY();
		double distance = Math.hypot(dx, dy);

		double step = speed * deltaTime;

		if (distance <= step || distance == 0) {
			sprite.setCenterX(target.getX());
			sprite.setCenterY(target.getY());
			currentNode = target;
			targetIndex++;
		} else {
			sprite.setCenterX(sprite.getCenterX() + dx / distance * step);
			sprite.setCenterY(sprite.getCenterY() + dy / distance * step);
		}
		if (onMoved != null) {
			onMoved.run();
		}
	}

	/** @return true als de huidige pad-knooppen allemaal zijn afgelegd */
	public boolean hasFinishedPath() {
		return path == null || targetIndex >= path.size();
	}

	/**
	 * De huidige stop is bereikt. Eerst de aankomst-callback.
	 * Vroeg die om een wachttijd, dan blijft de persoon staan, doet daarna de handeling
	 * en loopt pas daarna naar de volgende stop.
	 */
	private void onPathFinished() {
		Node arrived = currentDestination;
		pendingDwellSeconds = 0;
		pendingDwellAction = null;
		if (onArrived != null && arrived != null) {
			onArrived.accept(arrived);
		}
		double seconds = pendingDwellSeconds;
		Runnable action = pendingDwellAction;
		pendingDwellSeconds = 0;
		pendingDwellAction = null;
		if (seconds > 0) {
			dwell(seconds, () -> {
				if (action != null) {
					action.run();
				}
				routeToNextDestination();
			});
			return;
		}
		routeToNextDestination();
	}

	/** Koppelt een tooltip die bij hover de rol en de naam toont, ook op hoofd, romp en ledematen. */
	protected void setupTooltip() {
		if (sprite == null) {
			return;
		}
		tooltip = new Tooltip();
		tooltip.setShowDelay(Duration.millis(40));
		tooltip.setShowDuration(Duration.seconds(30));
		Tooltip.install(sprite, tooltip);
		for (javafx.scene.Node part : sprite.getChildren()) {
			Tooltip.install(part, tooltip);
		}
		updateTooltipText();
	}

	/** Zet de hovertekst op rol en naam. */
	private void updateTooltipText() {
		if (tooltip != null) {
			tooltip.setText(getRoleLabel());
		}
	}
}
