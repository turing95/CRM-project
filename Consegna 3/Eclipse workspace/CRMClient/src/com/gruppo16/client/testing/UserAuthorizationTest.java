package com.gruppo16.client.testing;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.rmi.interfaces.CRMServerInterface;

/**
 * Con questo test controllo che il server lanci
 * l'{@code Exception UnathorizedUserException} in caso di azioni non
 * autorizzate
 * 
 * @author gruppo16
 *
 */
public class UserAuthorizationTest {

	CRMServerInterface serverConnection;
	Employee loggedEmployee;

	private static final String rmiURL = "localhost";
	private static final int rmiPort = 12345;

	@Before
	public void setUp() throws Exception {
		// Mi connetto al server e effettuo il login con un profilo non manager
		Registry registry = LocateRegistry.getRegistry(rmiURL, rmiPort);
		serverConnection = (CRMServerInterface) registry.lookup("CRMServer");
		loggedEmployee = serverConnection.login("malta95", "pass".toCharArray());
	}

	/**
	 * Richiedo la lista degli impiegati non essendo loggato come manager
	 * 
	 * @throws RemoteException
	 * @throws UnathorizedUserException
	 * @throws ServerInternalErrorException
	 * @throws UnauthorizedClientException
	 */
	@Test(expected = UnathorizedUserException.class)
	public void testGetEmployees() throws RemoteException, UnathorizedUserException, ServerInternalErrorException {
		serverConnection.getEmployeeList(loggedEmployee);
	}

	/**
	 * Cerco di modificare i dati di un impiegato diverso da quello che ha
	 * effettuato il login
	 * 
	 * @throws RemoteException
	 * @throws ServerInternalErrorException
	 * @throws UnauthorizedClientException
	 * @throws UnathorizedUserException
	 */
	@Test(expected = UnathorizedUserException.class)
	public void testUpdateAnotherEmployeeValue()
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException {
		serverConnection.updateEmployeeValue(loggedEmployee, 2, "firstName", "test");
	}

	/**
	 * Provo a richiedere dei dati passando un istanza di Employee falsa
	 * 
	 * @throws RemoteException
	 * @throws ServerInternalErrorException
	 * @throws UnauthorizedClientException
	 * @throws UnathorizedUserException
	 */
	@Test(expected = UnathorizedUserException.class)
	public void testNotLoggedUser() throws RemoteException, ServerInternalErrorException, UnathorizedUserException {
		Employee test = new Employee("abs", "", 9, "1249184", "test@junit.com", "junit22", true);
		serverConnection.getCompanies(test, test.getEmployeeID());
	}

	@After
	public void tearDown() throws Exception {
		// effettuo il logout al termine del test
		serverConnection.logout(loggedEmployee);
	}

}
