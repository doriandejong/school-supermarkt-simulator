package com.example.schoolsupermarktsimulator.objects.registers;

import com.example.schoolsupermarktsimulator.objects.GameObject;

public abstract class Register extends GameObject {

	private int registerId;

	private boolean isOpen;

	public Register(double x, double y) {
        super(x,y);
    }

//	public void proceedToRegister(Customer c) {
//
//	}
//
//	public void assignCashier(Cashier c) {
//
//	}

}
