package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;
import com.gruppo16.crm.history.HistoryEntry;
import com.gruppo16.crm.rmi.interfaces.handlers.EmployeeHandlerInterface;
import com.gruppo16.crm.server.database.exceptions.EmployeeNotFoundException;
import com.gruppo16.crm.server.rmi.CRMServer;

public class EmployeeHandler implements EmployeeHandlerInterface {

	public EmployeeHandler(){}

	@Override
	public void changePassword(Employee loggedEmployee, int employeeID, char[] oldPassword, char[] newPassword)
			throws RemoteException, WrongOldPasswordException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().changeEmployeePassword(employeeID, newPassword, oldPassword);
		} catch (ClassNotFoundException | SQLException | EmployeeNotFoundException e) {
			e.printStackTrace(); 
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public void updateEmployeeValue(Employee loggedEmployee, int employeeID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().updateEmployee(employeeID, toUpdate, newValue);
		} catch (ClassNotFoundException | SQLException | InexistingColumnException e) {
			e.printStackTrace(); 
			throw new ServerInternalErrorException();
		}

	}

	@Override
	public int saveNewEmployee(Employee loggedEmployee, String name, String surname, String username, char[] password,
			String email, String phoneNumber, boolean isManager) throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertEmployee(name, surname, phoneNumber, email, username, password, isManager);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace(); 
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public void deleteEmployee(Employee loggedEmployee, Employee employee)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteEmployee(employee);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public ArrayList<Contact> searchContact(Employee loggedEmployee, String searchQuery, int employeeID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().searchContacts(searchQuery, employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public ArrayList<Company> searchCompany(Employee loggedEmployee, String searchQuery, int employeeID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().searchCompanies(searchQuery, employeeID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}
	
	@Override
	public void getEmployeeByID(Employee loggedEmployee, int employeeID) throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().getEmployeeByID(employeeID);
		} catch (ClassNotFoundException | SQLException | EmployeeNotFoundException e) {			
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}		
	}

	@Override
	public ArrayList<HistoryEntry> getHistory(Employee loggedEmployee) throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().getHistory();
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}
	
	@Override
	public ArrayList<Employee> getEmployeeList(Employee loggedEmployee) throws RemoteException,
			UnathorizedUserException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().getEmployeeList();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}

	}
	
}
