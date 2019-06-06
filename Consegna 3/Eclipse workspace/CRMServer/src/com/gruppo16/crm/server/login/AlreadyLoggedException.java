package com.gruppo16.crm.server.login;

public class AlreadyLoggedException extends Exception {

	private static final long serialVersionUID = -7603219225131987363L;

	protected AlreadyLoggedException() {
		super("L'utente ha già effettuato il login!");	
	}
		
}
