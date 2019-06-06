package com.gruppo16.crm.exceptions;

/**
 * Questa exception è lanciata in caso di login fallito.
 * @author gruppo 16
 *
 */
public class BadLoginException extends Exception {
	
	private static final long serialVersionUID = 2613821314343625755L;
	
	public BadLoginException(){
		super("Login fallito!");
	}
	
	public BadLoginException(String message){
		super(message);
	}

}
