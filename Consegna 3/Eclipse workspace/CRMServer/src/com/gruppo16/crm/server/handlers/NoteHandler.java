package com.gruppo16.crm.server.handlers;

import java.rmi.RemoteException;
import java.sql.SQLException;

import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.files.Note;
import com.gruppo16.crm.rmi.interfaces.handlers.NoteHandlerInterface;
import com.gruppo16.crm.server.rmi.CRMServer;

public class NoteHandler implements NoteHandlerInterface {
	
	public NoteHandler() {}

	@Override
	public int saveNewNote(CustomerType type, Employee loggedEmployee, String title, String content, int customerID)
			throws RemoteException, ServerInternalErrorException {
		try {
			return CRMServer.getDatabase().insertNote(type, customerID, title, content);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();			
		}
	}

	@Override
	public void updateNoteValue(Employee loggedEmployee, int noteID, String toUpdate, Object newValue)
			throws RemoteException, ServerInternalErrorException {		
		try {
			CRMServer.getDatabase().updateNote(noteID, toUpdate, newValue);
		} catch (ClassNotFoundException | SQLException | InexistingColumnException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}		
	}

	@Override
	public void deleteNote(Employee loggedEmployee, Note note) throws RemoteException, ServerInternalErrorException {
		try {
			CRMServer.getDatabase().deleteNote(note.getNoteID());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}		
	}
}
