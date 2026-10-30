package com.example.schoolsupermarktsimulator.world;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

public class NodeMap {
    private static final double MAP_WIDTH = 1280;
    private static final double MAP_HEIGHT = 720;

    private final List<Node> nodes = new ArrayList<>();
    public NodeMap() {
        createNodes();
        connectNodes();
    }

    private void createNodes() {
        nodes.add(new Node(0, 1300, 300)); // Buiten de voordeur, offscreen
        nodes.add(new Node(1, 1260, 300)); // Binnen de winkel, net door de voordeur
        nodes.add(new Node(2, 1220, 300)); // Eerste kruispunt, kan verder naar links of naar boven
        nodes.add(new Node(3, 1220, 150)); // Boven de voordeur, voor bovenste pad
        // Bovenste pad bij schappen
        nodes.add(new Node(4, 1120, 150));
        nodes.add(new Node(5, 990, 150));
        nodes.add(new Node(6, 880, 150));
        nodes.add(new Node(7, 840, 150));
        nodes.add(new Node(8, 800, 150));

        // Onderste pad bij schappen
        nodes.add(new Node(9, 1120, 300));
        nodes.add(new Node(10, 990, 300));
        nodes.add(new Node(11, 860, 300));

        // Van paden naar bakkerij
        nodes.add(new Node(12, 760, 300));
        nodes.add(new Node(13, 700, 170));

        // Bakkerij
        nodes.add(new Node(14, 700, 170));
        nodes.add(new Node(15, 650, 170));
        nodes.add(new Node(16, 600, 170));
        nodes.add(new Node(17, 530, 170));

        // Rechts van magazijndeur
        nodes.add(new Node(18, 680, 300));
        nodes.add(new Node(19, 500, 300));

        // Groente afdeling
        nodes.add(new Node(20, 500, 450));
        nodes.add(new Node(21, 500, 500));
        nodes.add(new Node(22, 500, 550));
        nodes.add(new Node(23, 500, 600));

        nodes.add(new Node(24, 680, 450));
        nodes.add(new Node(25, 680, 500));
        nodes.add(new Node(26, 680, 550));
        nodes.add(new Node(27, 680, 600));

        // Onder groente / midden voor kassa
        nodes.add(new Node(28, 500, 680));
        nodes.add(new Node(29, 680, 680));
        nodes.add(new Node(30, 800, 540));

        // Kassa
        nodes.add(new Node(31, 910, 540));
        nodes.add(new Node(32, 940, 540));
        nodes.add(new Node(33, 970, 540));
        nodes.add(new Node(34, 1000, 540));
        nodes.add(new Node(35, 1030, 540));
        nodes.add(new Node(36, 1080, 540));

        // Zelfscan links
        nodes.add(new Node(37, 880, 640));
        nodes.add(new Node(38, 910, 640));
        nodes.add(new Node(39, 950, 640));

        // Zelfscan rechts
        nodes.add(new Node(40, 950, 705));
        nodes.add(new Node(41, 1130, 705));
        nodes.add(new Node(42, 1160, 705));
        nodes.add(new Node(43, 1160, 640));

        // Naar uitgang
        nodes.add(new Node(44, 1200, 580));
        nodes.add(new Node(45, 1230, 580));
        nodes.add(new Node(46, 1300, 580));
    }

    private void connectNodes() {
        connect(0, 1);
        connect(1, 2);

        connectBidirectional(2, 3);
        connectBidirectional(2, 9);

        // Bovenste pad bij schappen
        connectBidirectional(3, 4);
        connectBidirectional(4, 5);
        connectBidirectional(5, 6);
        connectBidirectional(6, 7);
        connectBidirectional(7, 8);

        // Onderste pad bij schappen
        connectBidirectional(9, 10);
        connectBidirectional(10, 11);

        // Van paden heen en weer naar bakkerij
        connectBidirectional(8, 12);
        connectBidirectional(11, 12);

        connectBidirectional(8, 13);
        connectBidirectional(11, 13);

        // Bakkerij
        connectBidirectional(13 ,14);
        connectBidirectional(14 ,15);
        connectBidirectional(15 ,16);
        connectBidirectional(16 ,17);

        // Rechts van magazijndeur
        connectBidirectional(11, 18);
        connectBidirectional(13, 18);
        connectBidirectional(17, 19);
        connectBidirectional(18, 19);

        // Groente afdeling
        connectBidirectional(19, 20);
        connectBidirectional(20, 21);
        connectBidirectional(21, 22);
        connectBidirectional(22, 23);
        connectBidirectional(18, 24);
        connectBidirectional(24, 25);
        connectBidirectional(25, 26);
        connectBidirectional(26, 27);

        // Onder/rechts van groente
        connectBidirectional(23, 28);
        connectBidirectional(27, 29);
        connectBidirectional(28, 29);
        connectBidirectional(29, 30);

        connectBidirectional(24, 30);
        connectBidirectional(25, 30);
        connectBidirectional(26, 30);
        connectBidirectional(27, 30);

        connectBidirectional(11, 30);

        // Kassa
        connectBidirectional(30, 31);
        connectBidirectional(31, 32);
        connectBidirectional(32, 33);
        connectBidirectional(33, 34);
        connectBidirectional(34, 35);
        connectBidirectional(35, 36);

        // Zelfscan links
        connect(30, 37);
        connect(37, 38);
        connect(38, 39);

        // Zelfscan rechts
        connect(30, 40);
        connect(40, 41);
        connect(41, 42);
        connect(42, 43);

        // Naar uitgang
        connect(39, 44);
        connect(43, 44);
        connect(44, 45);
        connect(45, 46);
    }

    private void connect(int idA, int idB) {
        Node a = getNode(idA);
        Node b = getNode(idB);
        if (a.getId() != idA) {
            throw new InputMismatchException("Could not find the right Node");
        }
        a.connect(b);
    }

    private void connectBidirectional(int idA, int idB) {
        connect(idA, idB);
        connect(idB, idA);
    }

    public Node getNode(int id) {
        return nodes.get(id);
    }

    public List<Node> getNodes() {
        return List.copyOf(nodes);
    }
}
