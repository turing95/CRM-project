package com.gruppo16.crm.rmi.interfaces;

import java.rmi.Remote;

import com.gruppo16.crm.rmi.interfaces.handlers.CustomerHandlerInterface;
import com.gruppo16.crm.rmi.interfaces.handlers.EmployeeHandlerInterface;
import com.gruppo16.crm.rmi.interfaces.handlers.EventHandlerInterface;
import com.gruppo16.crm.rmi.interfaces.handlers.LoginHandlerInterface;
import com.gruppo16.crm.rmi.interfaces.handlers.NoteHandlerInterface;
import com.gruppo16.crm.rmi.interfaces.handlers.ReminderHandlerInterface;

public interface CRMServerInterface extends Remote, EmployeeHandlerInterface, EventHandlerInterface,
		NoteHandlerInterface, ReminderHandlerInterface, CustomerHandlerInterface, LoginHandlerInterface {
}