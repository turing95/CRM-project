package com.gruppo16.crm.customers;

import java.util.ArrayList;

/**
 * 
 */
public class CustomerList implements java.io.Serializable {

	private static final long serialVersionUID = 1125039520324393464L;
	//ATTRIBUTI
	private int customerListID;
	private String description;
	private ArrayList<Customer> list = new ArrayList<Customer>();
	CustomerListType listType;
	
	//METODI	
	
	public CustomerList(int customerListID, String description, ArrayList<Customer> list, CustomerListType listType) {		
		this.customerListID = customerListID;
		this.description = description;
		this.list = list;
		this.listType = listType;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(obj instanceof CustomerList){//Due liste sono uguali se hanno lo stesso id o la stessa descrizione
			CustomerList anotherList = (CustomerList) obj;
			return this.customerListID == anotherList.customerListID
					|| this.description.equals(anotherList.description);
		}
		return false;
	}
	

	@Override
	public String toString() {
		return listType.toString() + ": " + description;
	}



	public int getCustomerListID() {
		return customerListID;
	}
	

	public void addCustomer(Customer customer) {
		list.add(customer);
	}

	public void deleteCustomer(Customer customer) {
		list.remove(customer);
	}

	public String getDescription() {
		return description;
	}

	public void setdescription(String description) {
		this.description = description;
	}

	public ArrayList<Customer> getList() {
		return list;
	}

	public Customer getCustomer(int index) {
		return list.get(index);
	}

	public CustomerListType getListType() {
		return listType;
	}
	
	

}