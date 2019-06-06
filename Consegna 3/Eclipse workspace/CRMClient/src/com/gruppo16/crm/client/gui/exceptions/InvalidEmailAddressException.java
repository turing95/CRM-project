package com.gruppo16.crm.client.gui.exceptions;

public class InvalidEmailAddressException extends Exception {
	
	private static final long serialVersionUID = -1776126356210636833L;

	public InvalidEmailAddressException() {
		super("L'indirizzo email inserito ha un formato non corretto!");
	}
}
