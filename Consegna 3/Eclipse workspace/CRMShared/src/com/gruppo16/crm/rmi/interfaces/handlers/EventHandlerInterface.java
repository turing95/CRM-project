package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

/*
 * 	private int eventID;
	private int customerID;
	private String description;
	private LocalDateTime date;	
 */

public interface EventHandlerInterface {
	
	public int saveNewConferenceCall(CustomerType type,Employee loggedEmployee, String description, LocalDate date, int customerID)
			throws RemoteException, ServerInternalErrorException;
	
	public int saveNewEmail(CustomerType type,Employee loggedEmployee, String subject, String sender, String recipient,
			String description, LocalDateTime date, int customerID) throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public int saveNewMeeting(CustomerType type,Employee loggedEmployee, String description, LocalDateTime date, int customerID)
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public int saveNewOrder(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date, int customerID, String productList, float price) 
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public int saveNewPhoneCall(CustomerType type,Employee loggedEmployee, String description, LocalDateTime date, int customerID)
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;
	
	public void removeEvent(Employee loggedEmployee, Event event) 
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException; 
	
	public void updateEventValue(Employee loggedEmployee, Event event, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, InexistingColumnException,  UnathorizedUserException;

}
