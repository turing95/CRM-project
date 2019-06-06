package com.gruppo16.crm.employees;

import java.util.ArrayList;

import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.exceptions.CustomerNotFoundException;

/**
 * 
 */
public class Employee implements java.io.Serializable {

	// ATTRIBUTI
	private static final long serialVersionUID = 1436030762223871291L;

	/**
	 * ID dell'impiegato, uguale alla <code>PRIMARY KEY</code> nella tabella del
	 * database
	 */
	private final int employeeID;
	/**
	 * Se questa variabile è {@code true}, si tratta di un manager, che ha
	 * privilegi di creazione e modifica sugli altri impiegati
	 */
	private final boolean isManager;

	private String firstName;
	private String surname;
	private String phoneNumber;
	private String email;
	/** Username usato per l'accesso al sistema */
	private String username;

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/** Lista di clienti assegnati all'impiegato */
	private ArrayList<Customer> assignedCustomers = new ArrayList<Customer>();

	// METODI
	/** Costruttore con lista di clienti assegnata */
	public Employee(String firstName, String surname, int employeeID, String phoneNumber, String email,
			ArrayList<Customer> assignedCustomers, String username, boolean isManager) {
		this.firstName = firstName;
		this.surname = surname;
		this.employeeID = employeeID;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.assignedCustomers = assignedCustomers;
		this.username = username;
		this.isManager = isManager;
	}

	/** Costruttore senza lista di clienti assegnata */
	public Employee(String name, String surname, int employeeID, String phoneNumber, String email, String username,
			boolean isManager) {
		this.firstName = name;
		this.surname = surname;
		this.employeeID = employeeID;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.username = username;
		this.isManager = isManager;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + employeeID;
		result = prime * result + (isManager ? 1231 : 1237);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc) Verifico che siano uguali: id e username, poichè sono
	 * caratteristiche univoche; la variabile isManager, per evitare tentativi
	 * di accesso ad azioni non autorizzate.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Employee))
			return false;
		Employee other = (Employee) obj;
		if (employeeID != other.employeeID)
			return false;
		if (isManager != other.isManager)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public boolean isManager() {
		return isManager;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getSurname() {
		return surname;
	}

	public String getName() {
		return firstName + " " + surname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public void assignCustomer(Customer customer) {
		assignedCustomers.add(customer);
	}

	public void deleteCustomer(int customerID) throws CustomerNotFoundException {
		deleteCustomer(getAssignedCustomerById(customerID));
	}

	public void deleteCustomer(Customer customer) throws CustomerNotFoundException {
		if (!assignedCustomers.remove(customer))
			throw new CustomerNotFoundException();
	}

	public ArrayList<Customer> getAssignedCustomers() {
		return assignedCustomers;
	}

	public Customer getAssignedCustomerById(int customerID) throws CustomerNotFoundException {
		for (Customer customer : assignedCustomers) {
			if (customer.getCustomerID() == customerID)
				return customer;
		}
		throw new CustomerNotFoundException();
	}
}