package com.gruppo16.crm.exceptions;

/**
 * Lanciata se si chiama il metodo {@code logout()} ma nessun dipendente era loggato al client chiamante
 * @author malta
 *
 */
public class NoLoggedUserException extends Exception {
	
	private static final long serialVersionUID = -2103306450368402224L;

	public NoLoggedUserException() {
		super("Nessun utente e' loggato al sistema, impossibile eseguire il logout!");		
	}
	
	
	

}
