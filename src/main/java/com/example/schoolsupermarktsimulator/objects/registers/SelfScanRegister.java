package com.example.schoolsupermarktsimulator.objects.registers;

public class SelfScanRegister extends Register {

	public SelfScanRegister(double x, double y) {
		super(x, y);
	}

	public boolean doRandomInspection() {
		return false;
	}

}
