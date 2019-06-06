package com.gruppo16.crm.customers;

import java.time.LocalDate;

//ABSTRACT PRODUCT
public abstract class Customer implements java.io.Serializable{

	private static final long serialVersionUID = -637479743632843562L;
	private int customerID;
	private LocalDate contractStipulation;
	private BusinessState businessState;	
	//private ArrayList<Event> events;//Forse non è necessario farla, la lista di eventi è nel database

	public Customer(int id, LocalDate contractStipulation, BusinessState businessState){
		this.customerID = id;		
		this.contractStipulation = contractStipulation;		
		this.businessState = businessState;
	}
	
	@Override 
	public boolean equals(Object anotherObject){
		if(this == anotherObject)
			return true;		
		if(anotherObject instanceof Customer){
			if(anotherObject instanceof Company){				
				Company other = (Company) anotherObject;
				return other.equals(this);			
			}else if(anotherObject instanceof Contact){
				Contact other = (Contact) anotherObject;
				return other.equals(this);
			}				
		}
		return false;
	}
	
	/**Faccio l'override nelle due sottoclassi: se è un'azienda ritorna nome dell'azienda, se è una persona nome e cognome del cliente*/
	public abstract String getName();
	
	
	public int getCustomerID() {
		return customerID;
	}
	
	
	

	public void setStato(BusinessState businessState) {
		this.businessState = businessState;
	}

	
	public LocalDate getContractStipulation(){
		return contractStipulation;
	}
	
	
	public void setContractStipulation(LocalDate contractStipulation){
		this.contractStipulation = contractStipulation;
	}

//	public ArrayList<Event> getEventList() {
//		return events;
//	}

	public abstract String getEmail();
	public abstract String getAddress();
	public abstract String getPhoneNumber();
	
	
	public BusinessState getState() {
		return businessState;
	}

	@Override
	public String toString() {
		if(this instanceof Company)
			return ((Company) this).toString();
		if(this instanceof Contact)
			return ((Contact) this).toString();
		return super.toString();
	}
	
	

}