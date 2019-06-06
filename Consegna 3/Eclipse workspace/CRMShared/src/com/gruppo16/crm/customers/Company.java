package com.gruppo16.crm.customers;

import java.time.LocalDate;

public class Company extends Customer implements java.io.Serializable{

	private static final long serialVersionUID = 6376212047709830770L;
	//ATTRIBUTI:
	private String companyName;
	private String address;
	private String partitaIva;
	private String email;
	private String phoneNumber;

	//METODI:
	public Company(int id, LocalDate contractStipulation, BusinessState businessState,
			String companyName, String address, String partitaIva, String email, String phoneNumber) {
		super(id, contractStipulation, businessState);
		this.companyName = companyName;
		this.address = address;
		this.partitaIva = partitaIva;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public boolean equals(Object anotherObject) {
		if(this == anotherObject)
			return true;
		if(anotherObject instanceof Company){
			Company other = (Company) anotherObject;
			return this.getCustomerID() == other.getCustomerID();
		}
		return false;
	}


	
	
	
	@Override
	public String getName(){
		return companyName;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	@Override
	public String getAddress() {
		return address;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	@Override
	public String getEmail() {
		return email;
	}
	
	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String toString() {
		return companyName;
	}
	
	

}