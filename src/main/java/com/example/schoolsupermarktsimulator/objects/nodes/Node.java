package com.example.schoolsupermarktsimulator.objects.nodes;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final int id;
    private final double xPos;
    private final double yPos;

    private final List<Node> neighbors = new ArrayList<>();

    public Node(int id, double x, double y) {
        this.id = id;
        this.xPos = x;
        this.yPos = y;
    }

    public void connect(Node newConnection) {
        if (!neighbors.contains(newConnection)) {
            neighbors.add(newConnection);
        }
    }

    public boolean disconnect(Node removeConnection) {
        return neighbors.remove(removeConnection);
    }

    public int getId() {
        return id;
    }

    public String getIdAsString() {
        return String.valueOf(id);
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }
}
