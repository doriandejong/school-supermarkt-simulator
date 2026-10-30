package com.example.schoolsupermarktsimulator.objects.people;

import com.example.schoolsupermarktsimulator.objects.nodes.Node;

public class Employee extends Person {

	private int employeeId;

	public Employee(Node startNode, int id, String name) {
		super(startNode, id, name);
		this.employeeId = id;
	}
}
