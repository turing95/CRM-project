package com.gruppo16.crm.server.login;

public class NotLoggedException extends Exception {

	private static final long serialVersionUID = 6418105389847404142L;

	public NotLoggedException() {
		super("Errore! L'impiegato non aveva effettuato il login!");
	}

}
