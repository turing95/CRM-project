package com.gruppo16.crm.server.logger;

import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.history.HistoryType;

/**
 * 
 */
public class InfoLogger extends Logger {

	protected InfoLogger(int level){
		this.level = level;	
	}

	@Override
	protected void write(HistoryType logType, int employeeID, String message) throws ServerInternalErrorException {
		saveLog(logType, employeeID, message);
		System.out.println("Logger: " + message);
	}

}