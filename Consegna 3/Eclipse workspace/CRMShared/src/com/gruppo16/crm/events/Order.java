package com.gruppo16.crm.events;

import java.time.LocalDateTime;

/**
 * 
 */
public class Order extends Event {

	private static final long serialVersionUID = -2285677178125809087L;

	private float totalPrice;
	private String productList;// TODO se si ha tempo fare ArrayList<String>

	public Order(int eventID, int customerID, String description, LocalDateTime date, float totalPrice,
			String productList) {
		super(eventID, customerID, description, date);
		this.totalPrice = totalPrice;
		this.productList = productList;
	}

	public String getProductList() {
		return productList;
	}

	public float getTotalPrice() {
		return totalPrice;
	}
}