package com.gruppo16.crm.history;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HistoryEntry implements Serializable {

	private static final long serialVersionUID = 188847703823720037L;
	private int historyID;
	private HistoryType type;
	private int employeeID;
	private LocalDateTime date;
	private String description;

	public HistoryEntry(int historyID, HistoryType type, int employeeID, LocalDateTime date, String description) {
		super();
		this.historyID = historyID;
		this.type = type;
		this.employeeID = employeeID;
		this.date = date;
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getHistoryID() {
		return historyID;
	}

	public HistoryType getType() {
		return type;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

}
