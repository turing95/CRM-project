package com.gruppo16.crm.files;

public class Note implements java.io.Serializable{
	
	private static final long serialVersionUID = 4984638918395978795L;
	
	private int customerID;
	private int noteID;
	private String text;
	private String title;

	public Note(int customerID, int noteID, String title , String text) {
		this.customerID = customerID;
		this.noteID = noteID;
		this.text = text;
		this.title = title;
	}

	public int getCustomerID() {
		return customerID;
	}
	
	public int getNoteID() {
		return noteID;
	}

	public String getText() {
		return text;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return title;
	}
	
}