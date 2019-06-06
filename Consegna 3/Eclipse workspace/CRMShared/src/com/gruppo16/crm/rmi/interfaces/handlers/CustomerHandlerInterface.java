package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;
import java.time.LocalDate;
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
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.files.Note;

/**
 * Questa interfaccia definisce i metodi per la gestione del cliente e di tutti
 * gli eventi associati ad esso
 * 
 * @author malta
 *
 */
public interface CustomerHandlerInterface {

	public void deleteCustomer(Employee loggedEmployee, CustomerType type, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Event> getEvents(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Note> getNotes(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Reminder> getReminders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public int saveNewCompany(Employee loggedEmployee, String name, String partitaIva, String email, String phoneNumber,
			String address, LocalDate contractStipulation, BusinessState businessState, int employeeID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public int saveNewContact(Employee loggedEmployee, int companyID, String firstName, String surname, String email,
			String phoneNumber, String address, LocalDate contractStipulation, LocalDate contractEnd,
			BusinessState businessState, int employeeID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public int saveNewCustomerList(Employee loggedEmployee, String description, CustomerListType listType,
			ArrayList<Customer> list) throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public void updateCompanyValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public void updateContactValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<CustomerList> getCustomerLists(Employee loggedEmployee)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public void deleteCustomerList(Employee loggedEmployee, int customerListID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Order> getOrders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Contact> getContacts(Employee loggedEmployee, int employeeID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public ArrayList<Company> getCompanies(Employee loggedEmployee, int employeeID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

}
