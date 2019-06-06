package com.gruppo16.crm.events;

import java.time.LocalDateTime;

/**
 * 
 */
public class Meeting extends Event {

	private static final long serialVersionUID = -2539607002209931402L;

	public Meeting(int eventID, int customerID, String description, LocalDateTime date) {
		super(eventID, customerID, description, date);		
	}
	
	@Override
	public String toString() {
		return "Meetingm: " + super.toString();
	}
	
	
	
}