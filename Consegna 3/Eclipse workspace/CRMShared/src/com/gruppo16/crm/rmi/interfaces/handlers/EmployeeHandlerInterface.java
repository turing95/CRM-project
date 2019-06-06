package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;
import com.gruppo16.crm.history.HistoryEntry;

public interface EmployeeHandlerInterface {

	public void getEmployeeByID(Employee loggedEmployee, int employeeID) throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public void changePassword(Employee loggedEmployee, int employeeID, char[] oldPassword, char[] newPassword)
			throws RemoteException, WrongOldPasswordException, ServerInternalErrorException,  UnathorizedUserException;

	public void deleteEmployee(Employee loggedEmployee, Employee employee) 
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public ArrayList<Contact> searchContact(Employee loggedEmployee, String searchQuery, int employeeID) 
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public ArrayList<Company> searchCompany(Employee loggedEmployee, String searchQuery, int employeeID) 
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;

	public int saveNewEmployee(Employee loggedEmployee, String name, String surname, String username, char[] password, 
			String email, String phoneNumber, boolean isManager) 
					throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;

	public void updateEmployeeValue(Employee loggedEmployee, int employeeID, String toUpdate, Object newValue) 
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public ArrayList<HistoryEntry> getHistory(Employee loggedEmployee) throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public ArrayList<Employee> getEmployeeList(Employee loggedEmployee)
			throws RemoteException, UnathorizedUserException, ServerInternalErrorException;

	
}
