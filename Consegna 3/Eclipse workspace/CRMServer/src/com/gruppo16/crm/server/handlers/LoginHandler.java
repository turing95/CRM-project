package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.BadLoginException;
import com.gruppo16.crm.exceptions.NoLoggedUserException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.history.HistoryType;
import com.gruppo16.crm.rmi.interfaces.handlers.LoginHandlerInterface;
import com.gruppo16.crm.server.database.DBManager;
import com.gruppo16.crm.server.database.exceptions.EmployeeNotFoundException;
import com.gruppo16.crm.server.database.exceptions.UserNotFoundException;
import com.gruppo16.crm.server.logger.Logger;
import com.gruppo16.crm.server.rmi.CRMServer;

/**
 * La classe gestisce il login e il logout al server. Inoltre fornisce il metodo
 * {@code checkEmployee()}, necessario per controllare che ogni chiamata remota
 * sia compiuta da un client con utente loggato e con sufficienti privilegi. Le
 * istanze di ogni impiegato sono memorizzate in un {@code HashSet<Employee>}
 * per evitare login multipli. È pertanto necessario che la classe
 * {@code Employee} faccia override di {@code equals()}
 * 
 * @author gruppo16
 *
 */
public class LoginHandler implements LoginHandlerInterface {

	/**
	 * Passato come parametro al metodo {@code checkEmployee()}, indica che per
	 * il metodo richiesto sono richiesti privilegi da manager
	 */
	public static final int MANAGER_PRIVILEGE = 1;

	/**
	 * Passato come parametro al metodo {@code checkEmployee()}, indica che per
	 * il metodo richiesto sono richiesti privilegi da utente normale
	 */
	public static final int USER_PRIVILEGE = 0;

	public LoginHandler() {
	}

	/**
	 * Utilizzo la collection {@code Set} poichè non permette duplicati,
	 * impedendo login multipli
	 */
	private Set<Employee> loggedEmployees = new HashSet<Employee>();
	private Logger logger = Logger.getChainOfLoggers();

	@Override
	public Employee login(String username, char[] password)
			throws RemoteException, ServerInternalErrorException, BadLoginException {
		try {
			DBManager database = CRMServer.getDatabase();
			Employee loggedEmployee = database.checkLogin(username, password);
			if (loggedEmployees.add(loggedEmployee)) {
				logger.logMessage(Logger.INFO, HistoryType.Login, loggedEmployee.getEmployeeID(),
						"L'impiegato " + loggedEmployee.getName() + " ha effettuato il login");
				return loggedEmployee;
			} else {
				logger.logMessage(Logger.ERROR, HistoryType.Login, loggedEmployee.getEmployeeID(),
						"L'impiegato " + loggedEmployee.getName()
								+ " ha già una sessione di login attiva e ha cercato di aprirne un'altra!");
				throw new BadLoginException("Login fallito!");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		} catch (UserNotFoundException e) {
			logger.logMessage(Logger.ERROR, HistoryType.Login, 0, e.getMessage());
			throw new BadLoginException();
		} catch (EmployeeNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new BadLoginException();
		}
	}

	@Override
	public void logout(Employee loggedEmployee)
			throws RemoteException, NoLoggedUserException, ServerInternalErrorException {
		if (!loggedEmployees.remove(loggedEmployee))
			throw new NoLoggedUserException();
		logger.logMessage(Logger.INFO, HistoryType.Logout, loggedEmployee.getEmployeeID(),
				"L'impiegato " + loggedEmployee.getName() + " ha effettuato il logout");
	}

	/**
	 * Verifica l'impiegato sia loggato e autorizzato a compiere l'azione
	 * richiesta. Chiamare questa funzione all'inizio di ogni metodo remoto da
	 * verificare, eccetto {@code login()} e {@code logout()}
	 * 
	 * @param loggedEmployee
	 *            L'istanza di impiegato da verificare
	 * @param requiredPrivilege
	 *            Il privilegio richiesto per eseguire il metodo da
	 *            verificare:</br>
	 *            {@code MANAGER_PRIVILEGE} oppure {@code USER_PRIVILEGE}
	 * @throws UnathorizedUserException
	 *             Se l'utente loggato non ha i privilegi sufficienti per
	 *             eseguire l'azione, oppure se l'utente passato come parametro
	 *             non ha effettuato il login
	 * @throws ServerInternalErrorException
	 *             se si verificano errori durante l'esecuzione di metodi
	 *             interni al server
	 */
	public void checkEmployee(Employee loggedEmployee, int requiredPrivilege)
			throws UnathorizedUserException, ServerInternalErrorException {
		if (!loggedEmployees.contains(loggedEmployee))
			throw new UnathorizedUserException();
		int employeePrivilege = loggedEmployee.isManager() ? 1 : 0;
		if (employeePrivilege < requiredPrivilege) {
			logger.logMessage(Logger.ERROR, HistoryType.Error, loggedEmployee.getEmployeeID(),
					"Tentativo di esecuzione di un'operazione non autorizzata.");
			throw new UnathorizedUserException();
		}
	}
}
