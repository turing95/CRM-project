package com.gruppo16.crm.server.logger;

import java.sql.SQLException;

import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.history.HistoryType;
import com.gruppo16.crm.server.rmi.CRMServer;

public abstract class Logger {

	public static final int INFO = 1;
	public static final int ERROR = 2;

	protected int level;
	private Logger nextLogger;
	
	private static Logger chainOfLoggers;

	public void setNextLogger(Logger nextLogger) {
		this.nextLogger = nextLogger;
	}

	/**
	 * Si comporta come un singleton, verifica che esista l'istanza della
	 * catena dei logger, se non esiste la costruisce
	 * 
	 * @return
	 */
	public static Logger getChainOfLoggers() {
		if (chainOfLoggers == null) {
			InfoLogger infoLogger = new InfoLogger(INFO);
			ErrorLogger errorLogger = new ErrorLogger(ERROR);
			infoLogger.setNextLogger(errorLogger);
			chainOfLoggers = infoLogger;
		}
		return chainOfLoggers;
	}

	public void logMessage(int level, HistoryType logType, int employeeID, String message) throws ServerInternalErrorException {
		if (this.level == level) {
			write(logType, employeeID, message);
		}
		if (nextLogger != null) {
			nextLogger.logMessage(level, logType, employeeID, message);
		}

	}

	protected void saveLog(HistoryType logType, int employeeID, String message) throws ServerInternalErrorException {
		try {
			CRMServer.getDatabase().saveToHistory(logType, employeeID, message);
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Impossibile comunicare con il database per salvare il log!");
			e.printStackTrace();
			throw new ServerInternalErrorException();
		}
	}

	abstract protected void write(HistoryType logType, int employeeID, String message) throws ServerInternalErrorException;

}
