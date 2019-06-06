package com.gruppo16.crm.events;

import java.time.LocalDateTime;

public abstract class Event implements java.io.Serializable {

	private static final long serialVersionUID = 1911410564441681453L;

	private int eventID;
	private int customerID;
	private String description;
	private LocalDateTime date;	
	
	public Event(int eventID, int customerID, String description, LocalDateTime date) {
		super();
		this.eventID = eventID;
		this.customerID = customerID;
		this.description = description;
		this.date = date;
	}
	
	public int getEventID() {
		return eventID;
	}

	public String getDescription() {
		return description;
	}
	public LocalDateTime getDate() {
		return date;
	}

	public int getCustomerID() {
		return customerID;
	}

	@Override
	public String toString() {
		return description;
	}
}