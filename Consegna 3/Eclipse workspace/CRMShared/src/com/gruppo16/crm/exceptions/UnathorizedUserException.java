package com.gruppo16.crm.exceptions;

/**
 * Lanciata quando viene eseguita un'azione non permessa all'utente che ha effettuato il login
 * @author gruppo 16
 *
 */
public class UnathorizedUserException extends Exception {

	private static final long serialVersionUID = -3395293497797219049L;

	public UnathorizedUserException() {
		super("Utente non autorizzato!");		
	}

}
