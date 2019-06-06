package com.gruppo16.crm.client.core;

import java.util.ArrayList;

import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.exceptions.CustomerNotFoundException;

public class CustomerListsHandler {
	
	public static Customer getCustomerByID(CustomerType type, ArrayList<Customer> customerList, int customerID)
			throws CustomerNotFoundException {
		if (type == CustomerType.CONTACT) {
			for (Customer customer : customerList) {
				if (customer instanceof Contact) {
					if(customer.getCustomerID() == customerID)
						return customer;
				}
			}
		} else {
			for (Customer customer : customerList) {
				if (customer instanceof Company) {
					if(customer.getCustomerID() == customerID)
						return customer;
				}
			}
		}
		throw new CustomerNotFoundException();
	}

}
