package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;
import java.time.LocalDate;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public interface ReminderHandlerInterface {

	public void deleteReminder(Employee loggedEmployee, Reminder reminder)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public int saveNewReminder(CustomerType type, Employee loggedEmployee, String text, LocalDate date, int customerID)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

	public void updateReminderValue(Employee loggedEmployee, int reminderID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException, UnathorizedUserException;

}
