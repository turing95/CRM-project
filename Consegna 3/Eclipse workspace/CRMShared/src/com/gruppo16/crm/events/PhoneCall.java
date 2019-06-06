package com.gruppo16.crm.events;

import java.time.LocalDateTime;

public class PhoneCall extends Event {
	
	/** Impiegato con cui il cliente ha parlato */

	private static final long serialVersionUID = 5703930644060906874L;
		
	

	public PhoneCall(int eventID, int customerID, String description, LocalDateTime date) {
		super(eventID, customerID, description, date);		
	}



	@Override
	public String toString() {
		return "Telefonata: " + super.toString();
	}
	
	
	
}