package com.example.schoolsupermarktsimulator.objects.nodes;

import java.util.*;

public class Pathfinder {
    public static List<Node> findPath(Node start, Node goal) {
        if (start == null || goal == null) {
            // Er bestaat geen pad van/naar niks
            return Collections.emptyList();
        }

        // De kostenscore van de beweging, het doel is zo laag mogelijk zodat het weinig tijd kost
        Map<Node, Double> costScore = new HashMap<>();
        // Houdt de voortgang van het zoeken bij, waarbij elke node als value de vorige node heeft
        Map<Node, Node> cameFrom = new HashMap<>();

        // In deze queue alle nodes die nog bijlangs gegaan moeten worden om te kijken of er betere paths bestaan
        PriorityQueue<Node> openSet = new PriorityQueue<>(
                // Queue wordt gesorteerd op de kost om van 'start' naar 'current' te komen + de rechte lijn van 'current' naar 'goal'
                Comparator.comparingDouble(
                        node -> costScore.getOrDefault(node, Double.POSITIVE_INFINITY)
                                      + distance(node, goal)
                )
        );

        // In deze set all nodes die geen betere paths meer kunnen hebben
        Set<Node> closedSet = new HashSet<>();

        costScore.put(start, 0.0);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // Haal 'bovenste' node uit de set
            Node current = openSet.poll();

            // Als je bij goal aankomt, hele pad teruggeven
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            // Als current al in de closedSet is, de for-loop overslaan, door naar de volgende
            if (!closedSet.add(current)) {
                continue;
            }

            // Elke neighbor van de current node bijlangsgaan
            for (Node neighbor : current.getNeighbors()) {
                // Als de neighbor geen onbekende paths meer heeft, overslaan
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // tentativeCost is hetzelfde als de PriorityQueue sorteer methode, bekende kosten om bij 'current' te komen + verwachte afstand van 'current' naar 'neighbor'
                double tentativeCost = costScore.get(current) + distance(current, neighbor);
                // Als de totale verwachte kost lager is dan de bekende kosten (dus als de neighbor nog op kosten 'INFINITY' staat)
                if (tentativeCost < costScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    // neighbor wordt de nieuwe current
                    cameFrom.put(neighbor, current);
                    // geef de neighbor de juiste cost
                    costScore.put(neighbor, tentativeCost);

                    // 'Refresh' de nieuwe neighbor node, zodat PriorityQueue de juiste costScore gebruikt
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        // Er bestaat geen route van start naar goal
        return Collections.emptyList();
    }

    private static double distance(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        // Afstand tussen twee punten is wortel(kwadraat dx + kwadraat dy)
        return Math.hypot(dx, dy);
    }

    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            // 'current' wordt vervangen door de vorige node, en aan het path toegevoegd
            current = cameFrom.get(current);
            path.add(current);
        }

        // Omdat het path van current naar begin loopt, eerst reversen voor de return
        Collections.reverse(path);
        return path;
    }
}
