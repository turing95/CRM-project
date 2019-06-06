package com.gruppo16.crm.server.database;

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
import com.gruppo16.crm.customers.Product;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.CustomerNotFoundException;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;
import com.gruppo16.crm.files.Note;
import com.gruppo16.crm.history.HistoryEntry;
import com.gruppo16.crm.history.HistoryType;
import com.gruppo16.crm.server.database.exceptions.EmployeeNotFoundException;
import com.gruppo16.crm.server.database.exceptions.UserNotFoundException;

/**
 * Questa interfaccia contiene tutti i metodi necessari per gestire i dati
 * relativi al progetto su un Database SQL.
 * 
 * @author gruppo16
 *
 */
public interface DBManager {

	public abstract void assignCustomer(CustomerType type, int customerID, int employeeID)
			throws SQLException, ClassNotFoundException;

	public abstract void changeEmployeePassword(int employeeID, char[] newPassword, char[] oldPassword)
			throws SQLException, WrongOldPasswordException, EmployeeNotFoundException;

	/**
	 * Controlla sul database la corrispondenza username e password
	 * 
	 * @param username
	 *            username da verificare
	 * @param password
	 *            password da verificare
	 * @return in caso di successo nella verifica, l'id del dipendente che ha
	 *         effettuato il login
	 * @throws SQLException,
	 *             ClassNotFoundException
	 * @throws UserNotFoundException
	 *             se i dati non corrispondono (password errata o username
	 *             inesistente)
	 * @throws EmployeeNotFoundException
	 */
	public abstract Employee checkLogin(String username, char[] password)
			throws SQLException, UserNotFoundException, EmployeeNotFoundException;

	/** Chiude la connessione al database. */
	public abstract void closeConnection() throws SQLException;

	public abstract void deleteCustomer(CustomerType type, int customerID) throws SQLException, ClassNotFoundException;

	public abstract void deleteCustomerList(int customerListID) throws SQLException, ClassNotFoundException;

	public abstract void deleteEmployee(Employee employee) throws SQLException, ClassNotFoundException;

	public abstract void deleteEmployee(int employeeID) throws SQLException, ClassNotFoundException;

	public abstract void deleteEvent(Event event) throws SQLException, ClassNotFoundException;

	public abstract void deleteNote(int noteID) throws SQLException, ClassNotFoundException;

	public abstract void deletePhoneCall(int phone_callID) throws SQLException, ClassNotFoundException;

	public abstract void deleteReminder(int reminderID) throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Company> getCompanies(int employeeID) throws SQLException, ClassNotFoundException;

	public abstract Company getCompanyById(int companyID) throws SQLException, CustomerNotFoundException;

	public abstract Customer getContactById(int customerID) throws SQLException, CustomerNotFoundException;

	public abstract ArrayList<Contact> getContacts(int employeeID) throws SQLException, ClassNotFoundException;

	/**
	 * Ritorna la lista dei clienti associati a un dipendente.
	 * 
	 * @param employeeID
	 *            id del dipendente.
	 * @return <code>ArrayList</code> contenente le istanze di
	 *         <code>Customer</code> dei clienti associati al dipendente.
	 * @throws SQLException,
	 *             ClassNotFoundException
	 * @throws CustomerNotFoundException
	 */
	public abstract CustomerList getCustomCustomerList(int customerListID)
			throws SQLException, CustomerNotFoundException;

	public abstract ArrayList<CustomerList> getCustomerLists() throws SQLException, CustomerNotFoundException;

	/**
	 * Recupera dal database un impiegato dato il suo ID
	 * 
	 * @param employeeID
	 * @return l'istanza <code>Employee</code> dell'impiegato cercato
	 * @throws SQLException,
	 *             ClassNotFoundException
	 * @throws EmployeeNotFoundException
	 *             se non esiste un impiegato con l'id passato per parametro
	 */
	public abstract Employee getEmployeeByID(int employeeID) throws SQLException, EmployeeNotFoundException;

	public abstract ArrayList<Employee> getEmployeeList() throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Event> getEvents(CustomerType type, int customerID)
			throws SQLException, ClassNotFoundException;

	public abstract ArrayList<HistoryEntry> getHistory() throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Note> getNotes(CustomerType type, int customerID)
			throws SQLException, CustomerNotFoundException;

	public abstract ArrayList<Order> getOrders(CustomerType type, int customerID)
			throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Reminder> getReminders(CustomerType type, int customerID)
			throws SQLException, CustomerNotFoundException;

	public abstract int insertCompany(String name, String partitaIva, String email, String phoneNumber, String address,
			LocalDate contractStipulation, BusinessState businessState, int employeeID)
			throws SQLException, ClassNotFoundException;

	public abstract int insertConferenceCall(CustomerType type, String description, LocalDate date, int customerID)
			throws SQLException, ClassNotFoundException;

	public abstract int insertContact(LocalDate contractStipulation, BusinessState businessState, String firstName,
			String surname, String email, String phoneNumber, String address, int employeeID)
			throws SQLException, ClassNotFoundException;

	public abstract int insertCustomerList(String description, CustomerListType listType, ArrayList<Customer> list)
			throws SQLException, ClassNotFoundException;

	public abstract int insertEmail(CustomerType type, String subject, String sender, String recipient,
			String description, LocalDateTime date, int customerID) throws SQLException, ClassNotFoundException;

	public abstract int insertEmployee(String name, String surname, String phoneNumber, String email, String username,
			char[] password, boolean isManager) throws SQLException, ClassNotFoundException;

	public abstract int insertMeeting(CustomerType type, String description, LocalDateTime date, int customerID)
			throws SQLException, ClassNotFoundException;

	public abstract int insertNote(CustomerType type, int customerID, String title, String text)
			throws SQLException, ClassNotFoundException;

	public abstract int insertOrder(CustomerType type, int customerID, LocalDateTime date, String description,
			float price, String productList) throws SQLException, SQLException;

	public abstract int insertPhoneCall(CustomerType type, String description, LocalDateTime date, int customerID)
			throws SQLException, ClassNotFoundException;

	public abstract int insertProduct(Product product) throws SQLException, ClassNotFoundException;

	public abstract int insertReminder(CustomerType type, int customerID, LocalDate dateTime, String description)
			throws SQLException, ClassNotFoundException;

	public abstract void saveToHistory(HistoryType type, int employeeID, String message)
			throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Company> searchCompanies(String searchQuery, int employeeID)
			throws SQLException, ClassNotFoundException;

	public abstract ArrayList<Contact> searchContacts(String searchQuery, int employeeID)
			throws SQLException, ClassNotFoundException;

	public abstract void updateCompany(int companyID, String toUpdate, Object newValue)
			throws SQLException, CustomerNotFoundException, InexistingColumnException;

	public abstract void updateConferenceCall(int conferenceCallID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateContact(int customerID, String toUpdate, Object newValue)
			throws SQLException, CustomerNotFoundException, InexistingColumnException;

	public abstract void updateCustomerListDetail(int list_name, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateEmployee(int employeeID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateMeeting(int meetingID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateNote(int noteID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateOrder(int orderID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateOrderDetails(int orderID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updatePhoneCall(int phoneCallID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateProduct(int productID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

	public abstract void updateReminder(int reminderID, String toUpdate, Object newValue)
			throws SQLException, InexistingColumnException;

}
