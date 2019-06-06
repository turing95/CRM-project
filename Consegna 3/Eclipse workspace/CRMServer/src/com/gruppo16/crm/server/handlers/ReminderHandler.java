package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.rmi.interfaces.handlers.ReminderHandlerInterface;
import com.gruppo16.crm.server.rmi.CRMServer;

public class ReminderHandler implements ReminderHandlerInterface {
	
	public ReminderHandler() {}


	@Override
	public int saveNewReminder(CustomerType type, Employee loggedEmployee, String text, LocalDate date, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertReminder(type, customerID, date, text);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}


	@Override
	public void deleteReminder(Employee loggedEmployee, Reminder reminder)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteReminder(reminder.getReminderID());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}


	@Override
	public void updateReminderValue(Employee loggedEmployee,int reminderID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().updateReminder(reminderID, toUpdate, newValue);
		} catch (ClassNotFoundException | SQLException | InexistingColumnException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}
	
	
	

}
