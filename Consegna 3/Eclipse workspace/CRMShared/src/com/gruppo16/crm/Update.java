package com.gruppo16.crm;

public class Update implements java.io.Serializable {

	private static final long serialVersionUID = -8820951657711236787L;

	private String toUpdate;
	private Object value;

	public Update(String toUpdate, Object value) {		
		this.toUpdate = toUpdate;
		this.value = value;
	}

	public String getToUpdate() {
		return toUpdate;
	}

	public Object getValue() {
		return value;
	}

}
