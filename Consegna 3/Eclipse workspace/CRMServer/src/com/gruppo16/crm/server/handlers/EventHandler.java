package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.ConferenceCall;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Meeting;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.events.PhoneCall;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.rmi.interfaces.handlers.EventHandlerInterface;
import com.gruppo16.crm.server.rmi.CRMServer;

public class EventHandler implements EventHandlerInterface {

	public EventHandler() {
	}

	@Override
	public void removeEvent(Employee loggedEmployee, Event event)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteEvent(event);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}

	}

	@Override
	public int saveNewConferenceCall(CustomerType type, Employee loggedEmployee, String description, LocalDate date,
			int customerID) throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().insertConferenceCall(type, description, date, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
		return 0;
	}

	@Override
	public int saveNewEmail(CustomerType type, Employee loggedEmployee, String subject, String sender,
			String recipient, String description, LocalDateTime date, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().insertEmail(type, subject, sender, recipient, description, date, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
		return 0;
	}

	@Override
	public int saveNewMeeting(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID) throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().insertMeeting(type, description, date, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
		return 0;
	}


	@Override
	public int saveNewOrder(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID, String productList, float price) throws RemoteException, ServerInternalErrorException, UnathorizedUserException {
		try {
			return CRMServer.getDatabase().insertOrder(type, customerID, date, description, price, productList);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	@Override
	public int saveNewPhoneCall(CustomerType type, Employee loggedEmployee, String description, LocalDateTime date,
			int customerID) throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().insertPhoneCall(type, description, date, customerID);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
		return 0;
	}

	@Override
	public void updateEventValue(Employee loggedEmployee, Event event, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, InexistingColumnException {
		try {
			if (event instanceof PhoneCall) {
				CRMServer.getDatabase().updatePhoneCall(event.getEventID(), toUpdate, newValue);
			} else if (event instanceof ConferenceCall) {
				CRMServer.getDatabase().updateConferenceCall(event.getEventID(), toUpdate, newValue);
			} else if (event instanceof Meeting) {
				CRMServer.getDatabase().updateMeeting(event.getEventID(), toUpdate, newValue);
			} else if (event instanceof Order) {
				CRMServer.getDatabase().updateOrder(event.getEventID(), toUpdate, newValue);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

}
