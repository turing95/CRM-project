package com.gruppo16.crm.exceptions;

/**
 * Lanciata quando un client chiama il metodo {@code register()} sebbene si sia già registrato in precedenza sul server
 * @author malta
 *
 */
public class ClientAlreadyRegisteredException extends Exception {
	
	private static final long serialVersionUID = 760059591474768848L;

	public ClientAlreadyRegisteredException() {
		super("Client già registrato al server!");
	}
	
	

}
