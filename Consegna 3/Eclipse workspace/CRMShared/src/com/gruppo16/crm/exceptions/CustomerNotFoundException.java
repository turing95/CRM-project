package com.gruppo16.crm.exceptions;

public class CustomerNotFoundException extends Exception {

	private static final long serialVersionUID = 4223910332408980615L;

	public CustomerNotFoundException() {
		super("Cliente non trovato!");
	}


}
