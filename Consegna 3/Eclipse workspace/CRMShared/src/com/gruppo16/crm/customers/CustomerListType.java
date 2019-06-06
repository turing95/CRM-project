package com.gruppo16.crm.customers;

public enum CustomerListType {
	MAILING_LIST("Mailing list"),
	OPPORTUNITY_LIST("Lista clienti contattabili"),
	GROUP("Gruppo")
	;
	//ENUM('Mailing list','Lista clienti contattabili','Gruppo')

	private final String text;
	
	private CustomerListType(final String text) {
			this.text = text;
	}
	
	@Override
	public String toString() {		
		return text;
	}
		
}
