package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager. Loopt een ronde door de winkel tot iemand om hulp vraagt,
 * loopt dan daarnaartoe en hervat daarna de ronde.
 */
public class Manager extends Employee {

	private int peopleHelped;
	private final List<Node> round = new ArrayList<>();
	private int roundIndex;
	private boolean onCall;
	private final List<Call> waitingCalls = new ArrayList<>();

	/**
	 * @param startNode knoop waar de manager staat
	 * @param id personeelsnummer
	 * @param name naam van de manager
	 */
	public Manager(Node startNode, int id, String name) {
		super(startNode, id, name);
		sprite.setFill(Color.GOLD);
	}

	/** @return Manager, zichtbaar als je over het figuur hovert */
	@Override
	public String roleName() {
		return "Manager";
	}

	/**
	 * Helpt één persoon. Elke aanroep telt als één geholpen klant of collega.
	 */
	public void helpPerson() {
		peopleHelped++;
	}

	/** @return hoe vaak {@link #helpPerson()} is aangeroepen */
	public int getPeopleHelped() {
		return peopleHelped;
	}

	/** @return true zolang de manager naar een oproep onderweg is of iemand helpt */
	public boolean isOnCall() {
		return onCall;
	}

	/**
	 * Begint een ronde langs deze knopen en herhaalt die tot er een oproep komt.
	 *
	 * @param stops plekken in de winkel, in de volgorde van de ronde
	 */
	public void startRound(List<Node> stops) {
		round.clear();
		if (stops != null) {
			for (Node stop : stops) {
				if (stop != null) {
					round.add(stop);
				}
			}
		}
		roundIndex = 0;
		if (!onCall) {
			walkNextStop();
		}
	}

	/**
	 * Onderbreekt de ronde en loopt naar de plek waar iemand hulp vraagt.
	 * Staat de manager al bij een andere oproep, dan wacht deze tot die klaar is.
	 *
	 * @param where knoop van de persoon die roept
	 * @param whenThere wat er gebeurt zodra de manager daar is geweest, mag null zijn
	 */
	public void answerCall(Node where, Runnable whenThere) {
		if (where == null) {
			helpPerson();
			if (whenThere != null) {
				whenThere.run();
			}
			return;
		}
		waitingCalls.add(new Call(where, whenThere));
		if (!onCall) {
			dispatchNextCall();
		}
	}

	/** Loopt naar de volgende stop van de ronde. Een oproep gaat voor. */
	private void walkNextStop() {
		if (onCall || round.isEmpty()) {
			return;
		}
		Node next = round.get(roundIndex % round.size());
		roundIndex++;
		setOnRouteFinished(this::walkNextStop);
		setWalkingRoute(List.of(next));
	}

	/** Pakt de oudste oproep, loopt ernaartoe, helpt, en hervat daarna de ronde of de volgende oproep. */
	private void dispatchNextCall() {
		if (waitingCalls.isEmpty()) {
			onCall = false;
			walkNextStop();
			return;
		}
		onCall = true;
		Call call = waitingCalls.removeFirst();
		setOnRouteFinished(() -> dwell(WorkDuration.HELP_SECONDS, () -> {
			helpPerson();
			if (call.whenThere() != null) {
				call.whenThere().run();
			}
			dispatchNextCall();
		}));
		setWalkingRoute(List.of(call.where()));
	}

	/**
	 * Eén oproep: waar de manager naartoe moet en wat er daarna gebeurt.
	 *
	 * @param where knoop van de oproep
	 * @param whenThere vervolg nadat de manager geholpen heeft
	 */
	private record Call(Node where, Runnable whenThere) {
	}
}
