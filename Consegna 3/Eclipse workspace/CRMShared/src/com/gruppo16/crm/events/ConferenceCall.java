package com.gruppo16.crm.events;

import java.time.LocalDateTime;

/**
 * 
 */
public class ConferenceCall extends Event {	
	
	private static final long serialVersionUID = 7430450345416543895L;
	
	public ConferenceCall(String description, LocalDateTime date, int customerID, int eventID) {
		super(eventID, customerID, description, date);
	}
	
	@Override
	public String toString() {
		return "Conference call: " + super.toString();
	}
	
}