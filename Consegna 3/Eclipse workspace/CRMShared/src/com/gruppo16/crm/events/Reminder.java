package com.gruppo16.crm.events;

import java.time.LocalDate;

public class Reminder  implements java.io.Serializable{

	private static final long serialVersionUID = -6593616814252586442L;
	
	private int reminderID;
	private int customerID;
	private String content;
	private LocalDate expirationDate;
		
	public Reminder(int reminderID, int customerID, String content, LocalDate expirationDate) {	
		this.reminderID = reminderID;
		this.customerID = customerID;
		this.content = content;
		this.expirationDate = expirationDate;
	}
	
	

	public int getReminderID() {
		return reminderID;
	}



	public int getCustomerID() {
		return customerID;
	}

	public String getContent() {
		return content;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public boolean isExpired() {
		if(LocalDate.now().isAfter(expirationDate))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return content;
	}
}