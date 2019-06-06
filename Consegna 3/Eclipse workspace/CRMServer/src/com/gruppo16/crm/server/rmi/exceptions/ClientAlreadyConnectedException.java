package com.gruppo16.crm.server.rmi.exceptions;

public class ClientAlreadyConnectedException extends Exception {

	private static final long serialVersionUID = -73055350230538946L;

	public ClientAlreadyConnectedException() {
		super("Il client inserito era già connesso al server!");
	}

}
