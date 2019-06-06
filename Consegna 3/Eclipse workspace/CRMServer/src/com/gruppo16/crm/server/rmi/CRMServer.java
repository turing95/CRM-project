package com.gruppo16.crm.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.gruppo16.crm.customers.BusinessState;
import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.customers.CustomerList;
import com.gruppo16.crm.customers.CustomerListType;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.BadLoginException;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.NoLoggedUserException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;
import com.gruppo16.crm.files.Note;
import com.gruppo16.crm.history.HistoryEntry;
import com.gruppo16.crm.history.HistoryType;
import com.gruppo16.crm.rmi.interfaces.CRMServerInterface;
import com.gruppo16.crm.server.database.DBManager;
import com.gruppo16.crm.server.database.DBManagerFactory;
import com.gruppo16.crm.server.handlers.CustomerHandler;
import com.gruppo16.crm.server.handlers.EmployeeHandler;
import com.gruppo16.crm.server.handlers.EventHandler;
import com.gruppo16.crm.server.handlers.LoginHandler;
import com.gruppo16.crm.server.handlers.NoteHandler;
import com.gruppo16.crm.server.handlers.ReminderHandler;
import com.gruppo16.crm.server.logger.Logger;

/**
 * @author malta
 *
 */
public class CRMServer extends UnicastRemoteObject implements CRMServerInterface {

	private static final long serialVersionUID = 7787401541047824619L;

	private static final String dbURL = "jdbc:mysql://127.0.0.1:3306/CRMData?useSSL=false";
	private static final String dbUser = "java";
	private static final String dbPassword = "java";
	// TODO sarebbe meglio creare un file di configurazione contenente le info
	// per la connessione al database

	private static DBManager database;

	private static void connectToDatabase() throws ClassNotFoundException, IllegalArgumentException, SQLException {
		if(database == null)
			database = DBManagerFactory.getDBManager(DBManagerFactory.MYSQL, dbURL, dbUser, dbPassword);
		System.out.println("Connesso al database MySQL!");
	}
	public static DBManager getDatabase() throws ClassNotFoundException, IllegalArgumentException, SQLException {
		if (database == null) {// La connessione al database non è ancora stata
								// stabilta
			connectToDatabase();// riprovo a connettermi
		}
		return database;
	}
	private CustomerHandler customerHandler = new CustomerHandler();
	private EmployeeHandler employeeHandler = new EmployeeHandler();
	private EventHandler eventHandler = new EventHandler();
	private NoteHandler noteHandler = new NoteHandler();
	private ReminderHandler reminderHandler = new ReminderHandler();

	private Logger logger = Logger.getChainOfLoggers();

	private LoginHandler loginHandler = new LoginHandler();

	protected CRMServer() throws RemoteException {
		super();
		try {
			connectToDatabase();
		} catch (ClassNotFoundException | IllegalArgumentException | SQLException e1) {
			System.err.println("Impossibile stabilire una connessione con il database!");
			e1.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server in chiusura!");
				try {
					database.closeConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void changePassword(Employee loggedEmployee, int employeeID, char[] oldPassword, char[] newPassword)
			throws RemoteException, WrongOldPasswordException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		if(!loggedEmployee.isManager() && employeeID != loggedEmployee.getEmployeeID())//Se non è il manager posso solo cambiare i miei dati
			throw new UnathorizedUserException();
		employeeHandler.changePassword(loggedEmployee, employeeID, oldPassword, newPassword);
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha cambiato la password.");
	}

	private void checkEmployee(Employee loggedEmployee, int requiredPrivilege)
			throws  UnathorizedUserException, ServerInternalErrorException {
		loginHandler.checkEmployee(loggedEmployee, requiredPrivilege);
	}

	@Override
	public void deleteCustomer(Employee loggedEmployee, CustomerType type, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		customerHandler.deleteCustomer(loggedEmployee, type, customerID);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha eliminato il cliente " + customerID);
	}

	@Override
	public void deleteCustomerList(Employee loggedEmployee, int customerListID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		customerHandler.deleteCustomerList(loggedEmployee, customerListID);
	}

	@Override
	public void deleteEmployee(Employee loggedEmployee, Employee employee) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.MANAGER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		employeeHandler.deleteEmployee(loggedEmployee, employee);
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha cancellato l'impiegato " + employee.getEmployeeID());

	}

	@Override
	public void deleteNote(Employee loggedEmployee, Note note) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		noteHandler.deleteNote(loggedEmployee, note);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha cancellato la nota " + note.getNoteID());

	}

	@Override
	public void deleteReminder(Employee loggedEmployee, Reminder reminder) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		reminderHandler.deleteReminder(loggedEmployee, reminder);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha cancellato il promemoria " + reminder.getReminderID());

	}

	public ArrayList<Company> getCompanies(Employee loggedEmployee, int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getCompanies(loggedEmployee, employeeID);
	}

	public ArrayList<Contact> getContacts(Employee loggedEmployee, int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getContacts(loggedEmployee, employeeID);
	}

	@Override
	public ArrayList<CustomerList> getCustomerLists(Employee loggedEmployee) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getCustomerLists(loggedEmployee);
	}

	@Override
	public void getEmployeeByID(Employee loggedEmployee, int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.MANAGER_PRIVILEGE);
		employeeHandler.getEmployeeByID(loggedEmployee, employeeID);
	}

	@Override
	public ArrayList<Employee> getEmployeeList(Employee loggedEmployee) throws RemoteException,
			UnathorizedUserException, ServerInternalErrorException {
		checkEmployee(loggedEmployee, LoginHandler.MANAGER_PRIVILEGE);
		return employeeHandler.getEmployeeList(loggedEmployee);
	}

	@Override
	public ArrayList<Event> getEvents(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getEvents(type, loggedEmployee, customerID);
	}

	@Override
	public ArrayList<HistoryEntry> getHistory(Employee loggedEmployee) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.MANAGER_PRIVILEGE);
		return employeeHandler.getHistory(loggedEmployee);
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public ArrayList<Note> getNotes(CustomerType type, Employee loggedEmployee, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getNotes(type, loggedEmployee, customerID);
	}

	@Override
	public ArrayList<Order> getOrders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getOrders(type, loggedEmployee, customerID);
	}

	@Override
	public ArrayList<Reminder> getReminders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.getReminders(type, loggedEmployee, customerID);
	}

	@Override
	public Employee login(String username, char[] password)
			throws RemoteException, BadLoginException, ServerInternalErrorException {
		return loginHandler.login(username, password);
	}

	@Override
	public void logout(Employee loggedEmployee)
			throws RemoteException, NoLoggedUserException, ServerInternalErrorException {
		loginHandler.logout(loggedEmployee);
	}

	@Override
	public void removeEvent(Employee loggedEmployee, Event event) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		eventHandler.removeEvent(loggedEmployee, event);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha cancellato l'evento " + event.getEventID());

	}

	@Override
	public int saveNewCompany(Employee loggedEmployee, String name, String partitaIva, String email, String phoneNumber,
			String address, LocalDate contractStipulation, BusinessState businessState, int employeeID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato una nuova azienda");
		return customerHandler.saveNewCompany(loggedEmployee, name, partitaIva, email, phoneNumber, address,
				contractStipulation, businessState, employeeID);

	}

	@Override
	public int saveNewConferenceCall(CustomerType type, Employee loggedEmployee, String description, LocalDate date,
			int customerID) throws RemoteException, ServerInternalErrorException {
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato una nuova conference call");
		return eventHandler.saveNewConferenceCall(type, loggedEmployee, description, date, customerID);
	}

	@Override
	public int saveNewContact(Employee loggedEmployee, int companyID, String firstName, String surname, String email,
			String phoneNumber, String address, LocalDate contractStipulation, LocalDate contractEnd,
			BusinessState businessState, int employeeID) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato un nuovo contatto");

		return customerHandler.saveNewContact(loggedEmployee, companyID, firstName, surname, email, phoneNumber,
				address, contractStipulation, contractEnd, businessState, employeeID);
	}

	@Override
	public int saveNewCustomerList(Employee loggedEmployee, String description, CustomerListType listType,
			ArrayList<Customer> list) throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return customerHandler.saveNewCustomerList(loggedEmployee, description, listType, list);
	}

	@Override
	public int saveNewEmail(CustomerType type, Employee loggedEmployee, String subject, String sender, String recipient,
			String description, LocalDateTime date, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		logger.logMessage(Logger.INFO, HistoryType.Creation, loggedEmployee.getEmployeeID(),
				"L'utente  " + loggedEmployee.getEmployeeID() + " ha salvato una nuova mail");

		return eventHandler.saveNewEmail(type, loggedEmployee, subject, sender, recipient, description, date,
				customerID);
	}

	@Override
	public int saveNewEmployee(Employee loggedEmployee, String name, String surname, String username, char[] password,
			String email, String phoneNumber, boolean isManager) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato un nuovo impiegato");
		return employeeHandler.saveNewEmployee(loggedEmployee, name, surname, username, password, email, phoneNumber,
				isManager);
	}

	@Override
	public int saveNewMeeting(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID) throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato un nuovo meeting");
		return eventHandler.saveNewMeeting(type, loggedEmployee, description, date, customerID);
	}

	@Override
	public int saveNewNote(CustomerType type, Employee loggedEmployee, String title, String content, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato una nuova nota");
		return noteHandler.saveNewNote(type, loggedEmployee, title, content, customerID);
	}

	@Override
	public int saveNewOrder(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID, String productList, float price) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato un nuovo ordine");
		return eventHandler.saveNewOrder(type, loggedEmployee, description, date, customerID, productList, price);
	}

	@Override
	public int saveNewPhoneCall(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID) throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		logger.logMessage(Logger.INFO, HistoryType.Creation, loggedEmployee.getEmployeeID(),
				"L'utente  " + loggedEmployee.getEmployeeID() + " ha salvato una nuova telefonata");
		return eventHandler.saveNewPhoneCall(type, loggedEmployee, description, date, customerID);
	}

	@Override
	public int saveNewReminder(CustomerType type, Employee loggedEmployee, String text, LocalDate date, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Creation, employeeID,
				"L'utente  " + employeeID + " ha salvato un nuovo promemoria");
		return reminderHandler.saveNewReminder(type, loggedEmployee, text, date, customerID);
	}

	@Override
	public ArrayList<Company> searchCompany(Employee loggedEmployee, String searchQuery, int employeeID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return employeeHandler.searchCompany(loggedEmployee, searchQuery, employeeID);
	}

	@Override
	public ArrayList<Contact> searchContact(Employee loggedEmployee, String searchQuery, int employeeID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		return employeeHandler.searchContact(loggedEmployee, searchQuery, employeeID);
	}

	@Override
	public void updateCompanyValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha modificato l'azienda " + customerID);
		customerHandler.updateCompanyValue(loggedEmployee, customerID, toUpdate, newValue);
	}

	@Override
	public void updateContactValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha modificato il contatto " + customerID);
		customerHandler.updateContactValue(loggedEmployee, customerID, toUpdate, newValue);
	}

	@Override
	public void updateEmployeeValue(Employee loggedEmployee, int employeeID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		if(!loggedEmployee.isManager() && employeeID != loggedEmployee.getEmployeeID())//Se non è il manager posso solo cambiare i miei dati
			throw new UnathorizedUserException();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha modficato l'impiegato " + employeeID);
		employeeHandler.updateEmployeeValue(loggedEmployee, employeeID, toUpdate, newValue);
	}

	@Override
	public void updateEventValue(Employee loggedEmployee, Event event, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, InexistingColumnException,
			 UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha modificato l'evento " + event.getEventID());
		eventHandler.updateEventValue(loggedEmployee, event, toUpdate, newValue);
	}

	@Override
	public void updateNoteValue(Employee loggedEmployee, int noteID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha salvato la nota " + noteID);
		noteHandler.updateNoteValue(loggedEmployee, noteID, toUpdate, newValue);
	}

	@Override
	public void updateReminderValue(Employee loggedEmployee, int reminderID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		checkEmployee(loggedEmployee, LoginHandler.USER_PRIVILEGE);
		int employeeID = loggedEmployee.getEmployeeID();
		logger.logMessage(Logger.INFO, HistoryType.Modification, employeeID,
				"L'utente  " + employeeID + " ha modificato il promemoria  " + reminderID);
		reminderHandler.updateReminderValue(loggedEmployee, reminderID, toUpdate, newValue);

	}
	
	

}
