package com.gruppo16.crm.server.database.exceptions;

public class UserNotFoundException extends Exception {
	
	private static final long serialVersionUID = 7241063716623821260L;

	public UserNotFoundException() {
		super("Combinazione username/password non trovata");
	}
	
	public UserNotFoundException(String message){
		super(message);
	}
}
