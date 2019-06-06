package com.gruppo16.crm.customers;

/**
 * 
 */
public class Product {
	private double price;
	private String name;
	private String productCode;
	private int productID;	
	
	public Product(float price, String description, int productID) {
		super();
		this.price = price;
		this.name = description;
		this.productID = productID;
	}

	public double getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

	public int getProductID() {
		return productID;
	}

	public String getProductCode() {
		return productCode;
	}
	

}