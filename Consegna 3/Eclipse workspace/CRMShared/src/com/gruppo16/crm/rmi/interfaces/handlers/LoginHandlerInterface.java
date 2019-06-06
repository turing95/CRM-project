package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;

import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.BadLoginException;
import com.gruppo16.crm.exceptions.NoLoggedUserException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;

/**
 * Fornisce metodi per eseguire login e logout al server.
 * 
 * @author gruppo16
 *
 */
public interface LoginHandlerInterface {

	/**
	 * Chiamata dal client per effettuare il login al server. Controlla che la
	 * combinazione username/password esista sul database e che l'utente non
	 * abbia già una sessione di login attiva
	 * 
	 * @param username
	 *            username dell'utente
	 * @param password
	 *            password in array di char
	 * @return istanza {@code Employee} dell'utente che ha effettuato il login
	 * @throws RemoteException
	 * @throws ServerInternalErrorException
	 *             Se si incontrano errori interni al server (errori di
	 *             comunicazione con il database)
	 * @throws BadLoginException
	 *             Se il login non ha successo a causa di dati errati o di
	 *             sessione già presente
	 */
	public Employee login(String username, char[] password)
			throws RemoteException, ServerInternalErrorException, BadLoginException;

	/**
	 * Rimuove la sessione di login dell'utente specificato come parametro
	 * 
	 * @param employee
	 *            Istanza dell'impegato che effettua il logout
	 * @throws RemoteException
	 * @throws NoLoggedUserException
	 *             Se l'impiegato passato come parametro non ha una sessione di
	 *             login attiva
	 * @throws ServerInternalErrorException
	 */
	public void logout(Employee loggedEmployee)
			throws RemoteException, NoLoggedUserException, ServerInternalErrorException;

}
