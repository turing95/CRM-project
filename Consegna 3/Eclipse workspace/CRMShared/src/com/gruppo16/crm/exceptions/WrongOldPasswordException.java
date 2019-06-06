package com.gruppo16.crm.exceptions;


/**
 * Lanciata quando si cerca di cambiare la propria password ma la vecchia password inserita non è valida
 * @author malta
 *
 */
public class WrongOldPasswordException extends Exception {

	private static final long serialVersionUID = 808091992618845507L;

	public WrongOldPasswordException() {
		super("La vecchia password è errata!");
	}

}
