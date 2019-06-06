package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;
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
import com.gruppo16.crm.exceptions.CustomerNotFoundException;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.files.Note;
import com.gruppo16.crm.rmi.interfaces.handlers.CustomerHandlerInterface;
import com.gruppo16.crm.server.rmi.CRMServer;

public class CustomerHandler implements CustomerHandlerInterface {	

	public CustomerHandler() {}

	@Override
	public void deleteCustomer(Employee loggedEmployee, CustomerType type, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteCustomer(type, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}

	}

	@Override
	public ArrayList<Event> getEvents(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().getEvents(type, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public ArrayList<Note> getNotes(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().getNotes(type, customerID);
		} catch (ClassNotFoundException | SQLException | CustomerNotFoundException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException(e);
		}
	}

	@Override
	public ArrayList<Reminder> getReminders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().getReminders(type, customerID);
		} catch (ClassNotFoundException | SQLException | CustomerNotFoundException e) {
			e.printStackTrace();

			throw new ServerInternalErrorException();
		}
	}

	@Override
	public int saveNewCompany(Employee loggedEmployee, String name, String partitaIva, String email,
			String phoneNumber, String address, LocalDate contractStipulation, BusinessState businessState,
			int employeeID) throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertCompany(name, partitaIva, email, phoneNumber, address,
					contractStipulation, businessState, employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public int saveNewContact(Employee loggedEmployee, int companyID, String firstName, String surname, String email,
			String phoneNumber, String address, LocalDate contractStipulation, LocalDate contractEnd,
			BusinessState businessState, int employeeID) throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertContact(contractStipulation, businessState, firstName, surname, email,
					phoneNumber, address, employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public void updateCompanyValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().updateCompany(customerID, toUpdate, newValue);
		} catch (ClassNotFoundException | SQLException | CustomerNotFoundException | InexistingColumnException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public void updateContactValue(Employee loggedEmployee, int customerID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().updateContact(customerID, toUpdate, newValue);
		} catch (ClassNotFoundException | SQLException | CustomerNotFoundException | InexistingColumnException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public int saveNewCustomerList(Employee loggedEmployee, String description, CustomerListType listType,
			ArrayList<Customer> list) throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertCustomerList(description,listType, list);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public ArrayList<CustomerList> getCustomerLists(Employee loggedEmployee)
			throws RemoteException, ServerInternalErrorException {
			try {
				return CRMServer.getDatabase().getCustomerLists();
			} catch (ClassNotFoundException | SQLException | CustomerNotFoundException e) {
				e.printStackTrace();
				throw new ServerInternalErrorException();
			}
	}

	@Override
	public void deleteCustomerList(Employee loggedEmployee, int customerListID)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteCustomerList(customerListID);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServerInternalErrorException();
		}
		
	}

	@Override
	public ArrayList<Order> getOrders(CustomerType type, Employee loggedEmployee, int customerID)
			throws RemoteException, ServerInternalErrorException,
			UnathorizedUserException {
		try {
			return CRMServer.getDatabase().getOrders(type, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServerInternalErrorException();
		}
	}
	
	@Override
	public ArrayList<Contact> getContacts(Employee loggedEmployee, int employeeID) throws RemoteException,
			ServerInternalErrorException, UnathorizedUserException {
		try {
			return CRMServer.getDatabase().getContacts(employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public ArrayList<Company> getCompanies(Employee loggedEmployee, int employeeID) throws RemoteException,
			ServerInternalErrorException, UnathorizedUserException {
		try {
			return CRMServer.getDatabase().getCompanies(employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServerInternalErrorException();
		}
	}

	
	
	
}
