package com.gruppo16.crm.client.core.rmi;

import java.awt.Window;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.login.ConnectingToServerFrame;
import com.gruppo16.crm.client.gui.login.LoginDialog;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
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
import com.gruppo16.crm.rmi.interfaces.CRMServerInterface;

/**
 * 
 * @author gruppo16
 *
 */
public class CRMClient {

	private Employee loggedEmployee;

	/** Connessione al server remoto */
	private CRMServerInterface serverConnection;

	private static final String rmiURL = "localhost";
	private static final int rmiPort = 12345;

	private static CRMClient instance;

	private CRMClient() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Client in chiusura!");
				if(loggedEmployee != null)
					unregisterFromServer();
			}
		});
	}

	public static CRMClient getInstance() {
		if (instance == null) {
			instance = new CRMClient();
		}
		return instance;
	}

	private void connectToServer() {
		ConnectingToServerFrame.apri();
		System.out.println("Connessione al server...");
		try {			
			Registry registry = LocateRegistry.getRegistry(rmiURL, rmiPort);
			serverConnection = (CRMServerInterface) registry.lookup("CRMServer");
			System.out.println("Connesso al server!");
		} catch (RemoteException | NotBoundException e) {
			GUIUtils.showCriticalError(e, null);
		}finally{
			ConnectingToServerFrame.chiudi();
		}
	}

	public void changePassword(char[] oldPassword, char[] newPassword) throws WrongOldPasswordException,
			RemoteException, ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.changePassword(loggedEmployee, loggedEmployee.getEmployeeID(), oldPassword, newPassword);
	}

	public void changePassword(int employeeID, char[] oldPassword, char[] newPassword)
			throws RemoteException, WrongOldPasswordException, ServerInternalErrorException,
			 UnathorizedUserException {
		serverConnection.changePassword(loggedEmployee, employeeID, oldPassword, newPassword);
	}

	public void deleteCustomer(CustomerType type, int customerID) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		serverConnection.deleteCustomer(loggedEmployee, type, customerID);
	}

	public void deleteEmployee(Employee employee) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		serverConnection.deleteEmployee(loggedEmployee, employee);
	}

	public void deleteNote(Note note) throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		serverConnection.deleteNote(loggedEmployee, note);

	}
	

	public void deleteReminder(Reminder reminder) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {

		serverConnection.deleteReminder(loggedEmployee, reminder);
	}

	public ArrayList<Contact> getContacts(int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getContacts(loggedEmployee, employeeID);
	}

	public ArrayList<Company> getCompanies(int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getCompanies(loggedEmployee, employeeID);
	}

	public ArrayList<Employee> getEmployeeList() throws RemoteException, UnathorizedUserException,
			ServerInternalErrorException {
		return serverConnection.getEmployeeList(loggedEmployee);
	}

	public ArrayList<Event> getEvents(CustomerType type, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getEvents(type, loggedEmployee, customerID);
	}

	public Employee getLoggedEmployee() {
		return loggedEmployee;
	}

	public ArrayList<Note> getNotes(CustomerType type, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getNotes(type, loggedEmployee, customerID);
	}

	public ArrayList<Reminder> getReminders(CustomerType type, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getReminders(type, loggedEmployee, customerID);
	}

	public void login(String username, char[] password)
			throws BadLoginException, RemoteException, ServerInternalErrorException {
		if (serverConnection == null)
			connectToServer();
		loggedEmployee = serverConnection.login(username, password);
		EmployeeWindow.mostra(this);
	}

	public void logout(Window window) throws NoLoggedUserException, RemoteException, ServerInternalErrorException {
		serverConnection.logout(loggedEmployee);
		if(window != null)
			window.dispose();
		LoginDialog.apri();
	}

	public void removeEvent(Event event) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		serverConnection.removeEvent(loggedEmployee, event);
	}

	public int saveNewCompany(String name, String partitaIva, String email, String phoneNumber, String address,
			LocalDate contractStipulation, BusinessState businessState) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.saveNewCompany(loggedEmployee, name, partitaIva, email, phoneNumber, address,
				contractStipulation, businessState, loggedEmployee.getEmployeeID());
	}

	public int saveNewConferenceCall(CustomerType type, String description, LocalDate date, int customerID)
			throws RemoteException, ServerInternalErrorException {
		return serverConnection.saveNewConferenceCall(type, loggedEmployee, description, date, customerID);
	}

	public int saveNewContact(String firstName, String surname, String address, String email,
			LocalDate contractStipulation, LocalDate contractEnd, BusinessState businessState, String phoneNumber)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		return serverConnection.saveNewContact(loggedEmployee, 0, firstName, surname, email, phoneNumber, address,
				contractStipulation, contractEnd, businessState, loggedEmployee.getEmployeeID());
	}

	public int saveNewEmployee(String name, String surname, String username, char[] password, String email,
			String phoneNumber, boolean isManager) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		return serverConnection.saveNewEmployee(loggedEmployee, name, surname, username, password, email, phoneNumber,
				isManager);
	}

	public int saveNewMeeting(CustomerType type, String description, LocalDateTime date, int customerID)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		return serverConnection.saveNewMeeting(type, loggedEmployee, description, date, customerID);
	}

	public int saveNewNote(CustomerType type, String title, String content, int customerID)
			throws  UnathorizedUserException, RemoteException,
			ServerInternalErrorException {
		return serverConnection.saveNewNote(type, loggedEmployee, title, content, customerID);
	}

	public int saveNewOrder(CustomerType type, String description, LocalDateTime date, int customerID,
			String productList, float price) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		return serverConnection.saveNewOrder(type, loggedEmployee, description, date, customerID, productList, price);
	}

	public int saveNewPhoneCall(CustomerType type, String description, LocalDateTime date, int customerID)
			throws  UnathorizedUserException, RemoteException,
			ServerInternalErrorException {
		return serverConnection.saveNewPhoneCall(type, loggedEmployee, description, date, customerID);
	}

	public int saveNewReminder(CustomerType type, String text, LocalDate date, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.saveNewReminder(type, loggedEmployee, text, date, customerID);
	}

	public ArrayList<Company> searchCompany(String searchQuery, int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.searchCompany(loggedEmployee, searchQuery, employeeID);
	}

	public ArrayList<Contact> searchContact(String searchQuery, int employeeID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.searchContact(loggedEmployee, searchQuery, employeeID);
	}

	/**
	 * Chiamato in fase di chiusura del client, comunica al server la
	 * disconnessione
	 */
	private void unregisterFromServer() {
		try {
			serverConnection.logout(loggedEmployee);
		} catch (RemoteException | NoLoggedUserException | ServerInternalErrorException e) {
			GUIUtils.showCriticalError(e, null);
			e.printStackTrace();
		}
	}

	public void updateCompanyValue(int customerID, String toUpdate, Object newValue) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.updateCompanyValue(loggedEmployee, customerID, toUpdate, newValue);
	}

	public void updateContactValue(int customerID, String toUpdate, Object newValue) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.updateContactValue(loggedEmployee, customerID, toUpdate, newValue);
	}

	public void updateEmployeeValue(int employeeID, String toUpdate, Object newValue) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.updateEmployeeValue(loggedEmployee, employeeID, toUpdate, newValue);
	}

	public void updateEventValue(Event event, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, InexistingColumnException,
			 UnathorizedUserException {
		serverConnection.updateEventValue(loggedEmployee, event, toUpdate, newValue);
	}

	public void updateNoteValue(int noteID, String toUpdate, Object newValue) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.updateNoteValue(loggedEmployee, noteID, toUpdate, newValue);
	}

	public void updateReminderValue(int reminderID, String toUpdate, Object newValue) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		serverConnection.updateReminderValue(loggedEmployee, reminderID, toUpdate, newValue);
	}

	public ArrayList<HistoryEntry> getHistory() throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		return serverConnection.getHistory(loggedEmployee);
	}

	public int saveNewCustomerList(String description, CustomerListType listType, ArrayList<Customer> list)
			throws RemoteException, ServerInternalErrorException, 
			UnathorizedUserException {
		return serverConnection.saveNewCustomerList(loggedEmployee, description, listType, list);
	}

	public ArrayList<CustomerList> getCustomerLists() throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		return serverConnection.getCustomerLists(loggedEmployee);
	}

	public void deleteCustomerList(int customerListID) throws RemoteException, ServerInternalErrorException,
			 UnathorizedUserException {
		serverConnection.deleteCustomerList(loggedEmployee, customerListID);
	}

	public ArrayList<Order> getOrders(CustomerType type, int customerID) throws RemoteException,
			ServerInternalErrorException,  UnathorizedUserException {
		return serverConnection.getOrders(type, loggedEmployee, customerID);
	}

}
