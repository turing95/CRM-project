package com.gruppo16.crm.exceptions;

/**
 * Lanciata quando il parametro {@code toUpdate} non corrisponde a nessun valore
 * dell'oggetto che si cerca di aggiornare
 * 
 * @author malta
 *
 */
public class InexistingColumnException extends Exception {

	private static final long serialVersionUID = 1303121298822964208L;

	public InexistingColumnException(String argument) {
		super("Il parametro indicato in ingresso\"" + argument
				+ "\" non corrisponde a una colonna della tabella indicata!");
	}

}
