package com.gruppo16.crm.exceptions;

/**
 * Lanciata quando il formato di un numero di telefono inserito dall'utente è errato
 * @author malta
 *
 */
public class BadPhoneNumberException extends Exception {

	private static final long serialVersionUID = -6891502983048741043L;

	public BadPhoneNumberException() {
		super("Formato del numero telefonico inserito non valido!");
	}

}
