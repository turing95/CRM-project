package com.gruppo16.crm.server.rmi.exceptions;

public class ClientNotFoundException extends Exception {

	private static final long serialVersionUID = 2412499568406791846L;
	
	public ClientNotFoundException(){
		super("Client non trovato nella lista dei client connessi!");
	}

}
