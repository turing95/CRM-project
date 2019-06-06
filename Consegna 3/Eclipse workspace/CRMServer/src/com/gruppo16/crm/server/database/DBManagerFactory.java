package com.gruppo16.crm.server.database;

import java.sql.SQLException;

public class DBManagerFactory {

	/**
	 * Constante che indica la richiesta di instanziare una classe per la
	 * gestione di un database MySQL
	 */
	public static final int MYSQL = 1;	
	
	public static DBManager database;

	/**
	 * Consente di instanziare il giusto DBManager a seconda del tipo di
	 * database richiesto.
	 * 
	 * @param dbType
	 *            il tipo di database, da scegliere fra le
	 *            {@code public static final int} dichiarate in questa classe
	 * @param dbURL
	 *            L'URI di connessione al database
	 * @param dbUser
	 *            username per l'accesso al database
	 * @param dbPassword
	 *            Password per l'accesso al database
	 * @return L'istanza del {@code DBManager} corretto rispetto alla richiesta
	 * @throws ClassNotFoundException 
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 *             se il parametro inserito non corrisponde a uno dei DBManager
	 *             disponibili
	 */
	public static DBManager getDBManager(int dbType, String dbURL, String dbUser, String dbPassword)
			throws ClassNotFoundException, SQLException, IllegalArgumentException {
		// Eventuali altri DBManager devono implementare l'interfaccia DBManager
		// e gli deve essere associato un codice da aggiungere a questo switch
		if(database == null){
			switch (dbType) {
			case MYSQL:
				database = new MySQLManager(dbURL, dbUser, dbPassword);
				break;				
			default://Non esiste un gestore database associato al codice indicato come parametro
				throw new IllegalArgumentException("Il tipo di database " + dbType +" inserito come parametro non è stato riconosciuto");
			}
		}
		return database;
	}

}
