package com.gruppo16.crm.rmi.interfaces.handlers;

import java.rmi.RemoteException;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.files.Note;

public interface NoteHandlerInterface {

	public void deleteNote(Employee loggedEmployee, Note note)
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;

	int saveNewNote(CustomerType type, Employee loggedEmployee, String title, String content, int customerID)
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;

	public void updateNoteValue(Employee loggedEmployee, int noteID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException,  UnathorizedUserException;


}
