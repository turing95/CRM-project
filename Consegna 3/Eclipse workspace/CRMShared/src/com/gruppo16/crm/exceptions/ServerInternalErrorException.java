package com.gruppo16.crm.exceptions;

/**
 * Exception lanciata solitamente quando il server ha problemi di comunicazione con il database
 * @author gruppo16
 *
 */
public class ServerInternalErrorException extends Exception {
	
	private static final long serialVersionUID = 8619468299170528608L;

	public ServerInternalErrorException() {
		super("Errore interno al server!");
	}

	public ServerInternalErrorException(Throwable cause) {
		super(cause);	
	}
	
	
	
	

}
