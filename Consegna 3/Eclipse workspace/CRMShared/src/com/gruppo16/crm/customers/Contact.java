package com.gruppo16.crm.customers;

import java.time.LocalDate;
import java.util.ArrayList;

public class Contact extends Customer implements java.io.Serializable{
    
	private static final long serialVersionUID = 2597211195351176949L;
	//private CONTACT state;
	private String firstName;
	private String surname;
	private String address;
	private String email;	
	private String phoneNumber;
	private Company currentCompany;
	private ArrayList<Company> pastCompanies;
	
	
	/** Costruttore senza aziende per cui lavora in igresso*/
	public Contact(String firstName, String surname, String address, String email, int id, LocalDate contractStipulation,
			BusinessState businessState, String phoneNumber) {
		super(id, contractStipulation, businessState);
		this.firstName = firstName;
		this.surname = surname;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	
	
	/** Costruttore con azienda per cui lavora e aziende passate */
	public Contact(int id, LocalDate contractStipulation, BusinessState businessState, String firstName, String surname,
			String address, String email, String phoneNumber, Company currentCompany,
			ArrayList<Company> pastCompanies) {
		super(id, contractStipulation, businessState);
		this.firstName = firstName;
		this.surname = surname;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.currentCompany = currentCompany;
		this.pastCompanies = pastCompanies;
	}

	

	/** Costruttore con solo azienda per cui lavora */
	public Contact(int id, LocalDate contractStipulation, BusinessState businessState, String firstName, String surname,
			String address, String email, String phoneNumber, Company currentCompany) {
		super(id, contractStipulation, businessState);
		this.firstName = firstName;
		this.surname = surname;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.currentCompany = currentCompany;
	}

	


	@Override
	public boolean equals(Object anotherObject) {
		if(this == anotherObject)
			return true;
		if(anotherObject instanceof Contact){
			Contact other = (Contact) anotherObject;
			return this.getCustomerID() == other.getCustomerID();
		}
		return false;
	}


	@Override
	public String getAddress() {
		return address;
	}

	public Company getCurrentCompany() {
		return currentCompany;
	}
		
	@Override
	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}
	
	@Override
	public String getName(){//Restituisco nome e cognome
		return firstName + " " + surname;
	}

	public ArrayList<Company> getPastCompanies() {
		return pastCompanies;
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getSurname() {
		return surname;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}