package com.gruppo16.crm.client.core.factory;

import java.rmi.RemoteException;
import java.time.LocalDateTime;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.events.ConferenceCall;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Meeting;
import com.gruppo16.crm.events.PhoneCall;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class EventFactory {

	public EventFactory() {
	}

	public Event createEvent(CustomerType type, String eventType, int customerID, String description, LocalDateTime date) throws RemoteException, ServerInternalErrorException {
		int id = 0;
		switch (eventType) {
		case "Conference call":
			id = CRMClient.getInstance().saveNewConferenceCall(type, description, date.toLocalDate(), customerID);
			return new ConferenceCall(description, date, customerID, id);			
		case "Telefonata":
			try {
				id = CRMClient.getInstance().saveNewPhoneCall(type, description, date, customerID);
			} catch (UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, null);
				e.printStackTrace();
			}
			return new PhoneCall(id, customerID, description, date);		
		case "Meeting":
			try {
				id = CRMClient.getInstance().saveNewMeeting(type, description, date, customerID);
			} catch (UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, null);
				e.printStackTrace();
			}
			return new Meeting(id, customerID, description, date);			
		default:
			throw new IllegalArgumentException();
		}
	}

}