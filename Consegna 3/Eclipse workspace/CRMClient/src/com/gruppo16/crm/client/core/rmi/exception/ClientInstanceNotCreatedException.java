package com.gruppo16.crm.client.core.rmi.exception;

public class ClientInstanceNotCreatedException extends Exception {

	private static final long serialVersionUID = 7574976648521949443L;

	public ClientInstanceNotCreatedException() {
		super("L'istanza del client non è stata creata!");
	}


}
