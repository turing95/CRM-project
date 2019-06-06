package com.gruppo16.crm.server.database;

import java.io.CharArrayReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import com.gruppo16.crm.customers.BusinessState;
import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.customers.CustomerList;
import com.gruppo16.crm.customers.CustomerListType;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.customers.Product;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.events.ConferenceCall;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Meeting;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.events.PhoneCall;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.CustomerNotFoundException;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;
import com.gruppo16.crm.files.Note;
import com.gruppo16.crm.history.HistoryEntry;
import com.gruppo16.crm.history.HistoryType;
import com.gruppo16.crm.server.database.exceptions.EmployeeNotFoundException;
import com.gruppo16.crm.server.database.exceptions.UserNotFoundException;

/**
 * Questa classe si occupa di connettersi e di eseguire le query sul database
 * MySQL.
 * 
 * @author gruppo 16
 *
 */
public class MySQLManager implements DBManager {

	private Connection connection;
	private String URL;
	private String user;
	private String password;

	protected MySQLManager(String URL, String user, String password)
			throws   SQLException, ClassNotFoundException {
		this.URL = URL;
		this.user = user;
		this.password = password;
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(URL, user, password);
	}

	/**
	 * Chiamato all'inizio di ogni metodo per controllare che la connessione al
	 * database sia attiva. In caso negativo tenta di ristabilirla.
	 * 
	 * @throws ClassNotFoundException
	 * @throws 
	 *             SQLException
	 */
	public void checkConnection() throws   SQLException {
		if (connection == null || !connection.isValid(0))
			connection = DriverManager.getConnection(URL, user, password);
	}

	@Override
	public void assignCustomer(CustomerType type, int customerID, int employeeID)
			throws  SQLException, ClassNotFoundException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("UPDATE customer SET %s = ? WHERE id = ?;", column);
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, employeeID);
		stmt.setInt(2, customerID);
		stmt.execute();
		stmt.close();
	}

	@Override
	public void changeEmployeePassword(int employeeID, char[] newPassword, char[] oldPassword)
			throws  SQLException, WrongOldPasswordException, EmployeeNotFoundException {
		checkConnection();
		String sql = "SELECT password FROM employee WHERE id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, employeeID);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			if (!Arrays.equals(rs.getString("password").toCharArray(),oldPassword)) {
				stmt.close();
				rs.close();
				throw new WrongOldPasswordException();
			}
		} else {
			stmt.close();
			rs.close();
			throw new EmployeeNotFoundException("Non esiste impiegato con id " + employeeID);
		}
		performUpdateQuery("employee", "password", String.valueOf(newPassword), employeeID);
	}

	@Override
	public Employee checkLogin(String username, char[] password)
			throws  SQLException, UserNotFoundException, EmployeeNotFoundException {
		checkConnection();
		String sql = "SELECT id FROM employee WHERE username = ? AND password = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, username);
		stmt.setCharacterStream(2, new CharArrayReader(password));
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {// Se viene ritornata esiste l'username e la password è
						// corretta
			int employeeID = rs.getInt("id");
			rs.close();
			stmt.close();
			return getEmployeeByID(employeeID);
		}
		stmt.close();
		rs.close();
		throw new UserNotFoundException();
	}

	@Override
	public void closeConnection() throws SQLException {
		connection.close();
	}

	@Override
	public void deleteCustomer(CustomerType type, int customerID) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM ";
		sql += type == CustomerType.CONTACT ? "contact" : "company";
		sql += " WHERE id = ?;";
		executeDeleteQuery(sql, customerID);
	}

	@Override
	public void deleteCustomerList(int customerListID) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM customer_list_detail WHERE id = ?;";
		executeDeleteQuery(sql, customerListID);
	}

	@Override
	public void deleteEmployee(Employee employee) throws  SQLException {
		checkConnection();
		deleteEmployee(employee.getEmployeeID());
	}

	@Override
	public void deleteEmployee(int employeeID) throws  SQLException {
		checkConnection();
		String sql = "UPDATE employee SET is_active = ? WHERE id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setBoolean(1, false);
		stmt.setInt(2, employeeID);
		stmt.execute();
		stmt.close();
	}

	@Override
	public void deleteEvent(Event event) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM ";
		if (event instanceof PhoneCall) {
			sql += "phone_call";
		} else if (event instanceof Meeting) {
			sql += "meeting";
		} else if (event instanceof ConferenceCall) {
			sql += "conference_call";
		} else if (event instanceof Order) {
			sql += "`order`";
		} else
			throw new IllegalArgumentException();
		sql += " WHERE id = ?;";
		executeDeleteQuery(sql, event.getEventID());
	}

	@Override
	public void deleteNote(int noteID) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM note WHERE id = ?;";
		executeDeleteQuery(sql, noteID);
	}

	@Override
	public void deletePhoneCall(int phoneCallID) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM phone_call WHERE id = ?;";
		executeDeleteQuery(sql, phoneCallID);
	}

	@Override
	public void deleteReminder(int reminderID) throws  SQLException {
		checkConnection();
		String sql = "DELETE FROM reminder WHERE id = ?;";
		executeDeleteQuery(sql, reminderID);
	}

	private void executeDeleteQuery(String sql, int id) throws  SQLException {
		checkConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
	}

	@Override
	public ArrayList<Company> getCompanies(int employeeID) throws  SQLException {
		checkConnection();
		String sql = "SELECT * FROM company";
		if (employeeID == 0) {
			sql += ";";
		} else {
			sql += " WHERE employee_id = ?;";
		}
		PreparedStatement stmt = connection.prepareStatement(sql);
		if (employeeID != 0)
			stmt.setInt(1, employeeID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Company> companyList = new ArrayList<Company>();
		while (rs.next()) {
			int id = rs.getInt("id");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			BusinessState businessState = BusinessState.valueOf(rs.getString("business_state"));
			String companyName = rs.getString("name");
			String address = rs.getString("address");
			String partitaIva = rs.getString("partita_iva");
			String email = rs.getString("email");
			String phoneNumber = rs.getString("phone_number");
			companyList.add(new Company(id, contractStipulation, businessState, companyName, address, partitaIva, email,
					phoneNumber));
		}
		rs.close();
		stmt.close();
		return companyList;
	}

	@Override
	public Company getCompanyById(int companyID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM company WHERE id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, companyID);
		ResultSet rs = stmt.executeQuery();
		Company company;
		if (rs.next()) {
			int id = rs.getInt("id");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			BusinessState businessState = BusinessState.valueOf(rs.getString("business_state"));
			String companyName = rs.getString("name");
			String address = rs.getString("address");
			String phoneNumber = rs.getString("phone_number");
			String email = rs.getString("email");
			String partitaIva = rs.getString("partita_iva");
			company = new Company(id, contractStipulation, businessState, companyName, address, partitaIva, email,
					phoneNumber);
		} else
			throw new CustomerNotFoundException();
		return company;
	}

	@Override
	public Customer getContactById(int customerID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM contact WHERE id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		ResultSet rs = stmt.executeQuery();
		Contact contact;
		if (rs.next()) {
			int id = rs.getInt("id");
			// Company currentCompany = getCompanyById(rs.getInt("company_id"));
			String firstName = rs.getString("first_name");
			String surname = rs.getString("surname");
			String email = rs.getString("email");
			String address = rs.getString("address");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			BusinessState businessState = BusinessState.valueOf(rs.getString("business_state"));
			String phoneNumber = rs.getString("phone_number");
			// ArrayList<Company> pastCompanies = getPastCompaniesList(id);
			contact = new Contact(firstName, surname, address, email, id, contractStipulation, businessState,
					phoneNumber);
		} else
			throw new CustomerNotFoundException();
		return contact;
	}

	@Override
	public ArrayList<Contact> getContacts(int employeeID) throws  SQLException {
		checkConnection();
		String sql = "SELECT * FROM contact";
		sql += employeeID == 0 ? ";" : " WHERE employee_id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		if (employeeID != 0)
			stmt.setInt(1, employeeID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		while (rs.next()) {
			String firstName = rs.getString("first_name");
			String surname = rs.getString("surname");
			String address = rs.getString("address");
			String email = rs.getString("email");
			int id = rs.getInt("id");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			BusinessState state = BusinessState.valueOf(rs.getString("business_state"));
			String phoneNumber = rs.getString("phone_number");
			contactList
					.add(new Contact(firstName, surname, address, email, id, contractStipulation, state, phoneNumber));
		}
		stmt.close();
		rs.close();
		return contactList;
	}

	@Override
	public CustomerList getCustomCustomerList(int customerListID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM customer_list_detail WHERE id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerListID);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String description = rs.getString("description");
			ArrayList<Customer> list = getListCustomers(customerListID);
			stmt.close();
			rs.close();
			return new CustomerList(customerListID, description, list, null);
		} else
			throw new CustomerNotFoundException();
	}

	@Override
	public ArrayList<CustomerList> getCustomerLists()
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM customer_list_detail;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		ArrayList<CustomerList> list = new ArrayList<CustomerList>();
		while (rs.next()) {
			int customerListID = rs.getInt("id");
			String description = rs.getString("description");
			CustomerListType listType = CustomerListType.OPPORTUNITY_LIST;
			switch (rs.getString("list_type")) {
			case "Mailing list":
				listType = CustomerListType.MAILING_LIST;
				break;
			case "Gruppo":
				listType = CustomerListType.GROUP;
				break;
			case "Lista clienti contattabili":
				listType = CustomerListType.OPPORTUNITY_LIST;
				break;
			}
			ArrayList<Customer> customerList = getListCustomers(customerListID);
			list.add(new CustomerList(customerListID, description, customerList, listType));
		}
		return list;
	}

	@Override
	public Employee getEmployeeByID(int employeeID)
			throws  SQLException, EmployeeNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM employee WHERE id = ? AND is_active = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);// preparo la
																	// sql
		stmt.setInt(1, employeeID);
		stmt.setBoolean(2, true);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String phoneNumber = rs.getString("phone_number");
			String email = rs.getString("email");
			String username = rs.getString("username");
			boolean isManager = rs.getBoolean("is_manager");
			rs.close();
			stmt.close();
			return new Employee(name, surname, employeeID, phoneNumber, email, username, isManager);
		} else {
			rs.close();
			stmt.close();
			throw new EmployeeNotFoundException("Impossibile trovare l'impegato con id " + employeeID);
		}
	}

	@Override
	public ArrayList<Employee> getEmployeeList() throws  SQLException {
		checkConnection();
		String sql = "SELECT id, name, surname, username, phone_number, email, is_manager FROM employee WHERE is_active = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setBoolean(1, true);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Employee> list = new ArrayList<Employee>();
		while (rs.next()) {
			int employeeID = rs.getInt("id");
			String firstName = rs.getString("name");
			String surname = rs.getString("surname");
			String username = rs.getString("username");
			String phoneNumber = rs.getString("phone_number");
			String email = rs.getString("email");
			boolean isManager = rs.getBoolean("is_manager");
			Employee employee = new Employee(firstName, surname, employeeID, phoneNumber, email, username, isManager);
			list.add(employee);
		}
		rs.close();
		stmt.close();
		return list;
	}

	@Override
	public ArrayList<Event> getEvents(CustomerType type, int customerID) throws  SQLException {
		checkConnection();
		String column;
		if (type == CustomerType.COMPANY)
			column = "company_id";
		else
			column = "contact_id";
		String sql = String.format("SELECT * FROM phone_call WHERE %s = ?;", column);
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Event> list = new ArrayList<Event>();
		while (rs.next()) {
			int eventID = rs.getInt("id");
			String description = rs.getString("description");
			LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
			list.add(new PhoneCall(eventID, customerID, description, date));
		}
		stmt.close();
		rs.close();
		sql = String.format("SELECT * FROM meeting WHERE %s = ?;", column);
		stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		rs = stmt.executeQuery();
		while (rs.next()) {
			int eventID = rs.getInt("id");
			String description = rs.getString("description");
			LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
			list.add(new Meeting(eventID, customerID, description, date));
		}
		stmt.close();
		rs.close();
		sql = String.format("SELECT * FROM conference_call WHERE %s = ?;", column);
		stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		rs = stmt.executeQuery();
		while (rs.next()) {
			int eventID = rs.getInt("id");
			String description = rs.getString("description");
			LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
			list.add(new ConferenceCall(description, date, customerID, eventID));
		}
		return list;
	}

	@Override
	public ArrayList<HistoryEntry> getHistory() throws  SQLException {
		checkConnection();
		String sql = "SELECT * FROM employee_history;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		ArrayList<HistoryEntry> history = new ArrayList<HistoryEntry>();
		while (rs.next()) {
			int historyID = rs.getInt("id");
			HistoryType type = HistoryType.valueOf(rs.getString("type"));
			int employeeID = rs.getInt("employee_id");
			LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
			String description = rs.getString("description");
			history.add(new HistoryEntry(historyID, type, employeeID, date, description));
		}
		return history;
	}

	private ArrayList<Customer> getListCustomers(int customerListID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM customer_list WHERE list_detail_id = ? AND contact_id IS NOT NULL;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerListID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Customer> list = new ArrayList<Customer>();
		while (rs.next()) {
			int contactID = rs.getInt("contact_id");
			list.add(getContactById(contactID));
		}
		stmt.close();
		rs.close();
		sql = "SELECT * FROM customer_list WHERE list_detail_id = ? AND company_id IS NOT NULL;";
		stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerListID);
		rs = stmt.executeQuery();
		while (rs.next()) {
			int companyID = rs.getInt("company_id");
			list.add(getCompanyById(companyID));
		}
		stmt.close();
		rs.close();
		return list;
	}

	@Override
	public ArrayList<Note> getNotes(CustomerType type, int customerID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String column = CustomerType.CONTACT == type ? "contact_id" : "company_id";
		String sql = String.format("SELECT * FROM note WHERE %s = ?;", column);
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Note> noteList = new ArrayList<Note>();
		while (rs.next()) {
			String title = rs.getString("title");
			String text = rs.getString("text");
			int noteID = rs.getInt("id");
			noteList.add(new Note(customerID, noteID, title, text));
		}
		rs.close();
		stmt.close();
		return noteList;
	}

	@Override
	public ArrayList<Order> getOrders(CustomerType type, int customerID) throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("SELECT * FROM `order` WHERE %s = ?;", column);
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Order> list = new ArrayList<Order>();
		while (rs.next()) {
			int eventID = rs.getInt("id");
			String description = rs.getString("description");
			LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
			float totalPrice = rs.getFloat("price");
			String productList = rs.getString("product_list");
			list.add(new Order(eventID, customerID, description, date, totalPrice, productList));
		}
		return list;
	}

	private ArrayList<Company> getPastCompaniesList(int contactID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();
		String sql = "SELECT * FROM contact_past_companies WHERE contact_id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, contactID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Company> list = new ArrayList<Company>();
		while (rs.next()) {
			list.add(getCompanyById(rs.getInt("company_id")));
		}
		return list;
	}

	/**
	 * Dopo un {@code INSERT} passare il {@code PreparedStatement} a questa
	 * funzione per ricavare l'id del valore inserito. Inoltre chiude il
	 * {@code PreparedStatement} passato come parametro. NB: Passare come
	 * parametro {@code Statement.RETURN_GENERATED_KEYS}
	 * 
	 * @param stmt
	 *            Il {@code PreparedStatement} della query
	 * @return l'id {@code PRIMARY KEY} generato dal database
	 * @throws 
	 *             SQLException
	 */
	private int getPrimaryKey(PreparedStatement stmt) throws  SQLException {
		checkConnection();
		ResultSet rs = stmt.getGeneratedKeys();
		int id;
		if (rs.next()) {
			id = rs.getInt(1);
		} else
			throw new SQLException("Impossibile ricavare la chiave generata!");
		stmt.close();
		rs.close();
		return id;
	}

	@Override
	public ArrayList<Reminder> getReminders(CustomerType type, int customerID)
			throws  SQLException, CustomerNotFoundException {
		checkConnection();

		String sql = "SELECT * FROM reminder";
		sql += type == CustomerType.CONTACT ? " WHERE contact_id = ?;" : " WHERE company_id = ?;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, customerID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
		while (rs.next()) {
			String content = rs.getString("description");
			LocalDate expirationDate = rs.getDate("date").toLocalDate();
			int id = rs.getInt("id");
			reminderList.add(new Reminder(id, customerID, content, expirationDate));
		}
		rs.close();
		stmt.close();
		return reminderList;
	}

	@Override
	public int insertCompany(String name, String partitaIva, String email, String phoneNumber, String address,
			LocalDate contractStipulation, BusinessState businessState, int employeeID)
			throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO company(name, partita_iva, email, phone_number, address, contract_stipulation, business_state, employee_id) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setString(i++, name);
		stmt.setString(i++, partitaIva);
		stmt.setString(i++, email);
		stmt.setString(i++, phoneNumber);
		stmt.setString(i++, address);
		stmt.setObject(i++, contractStipulation);
		stmt.setString(i++, businessState.toString());
		stmt.setInt(i++, employeeID);
		stmt.execute();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertConferenceCall(CustomerType type, String description, LocalDate date, int customerID)
			throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO conference_call(description, date, %s) VALUES(?, ?, ?);", column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, description);
		stmt.setObject(2, date);
		stmt.setInt(3, customerID);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertContact(LocalDate contractStipulation, BusinessState businessState, String firstName,
			String surname, String email, String phoneNumber, String address, int employeeID)
			throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO contact(company_id, first_name, surname, email, phone_number, address, "
				+ "contract_stipulation, business_state, employee_id) VALUES(?, ?, ?, ?, ?, ? ,? ,? ,?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setInt(i++, 0);
		stmt.setString(i++, firstName);
		stmt.setString(i++, surname);
		stmt.setString(i++, email);
		stmt.setString(i++, phoneNumber);
		stmt.setString(i++, address);
		stmt.setObject(i++, contractStipulation);
		stmt.setString(i++, businessState.toString());
		stmt.setInt(i++, employeeID);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertCustomerList(String description, CustomerListType listType, ArrayList<Customer> list)
			throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO customer_list_detail(description, list_type) VALUES(?, ?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, description);
		stmt.setString(2, listType.toString());
		stmt.executeUpdate();
		int detailID = getPrimaryKey(stmt);
		for (Customer customer : list) {
			String column = "contact_id";
			if (customer instanceof Company)
				column = "company_id";
			sql = String.format("INSERT INTO customer_list(%s, list_detail_id) VALUES(?, ?);", column);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, customer.getCustomerID());
			stmt.setInt(2, detailID);
			stmt.execute();
		}
		stmt.close();
		return detailID;
	}

	@Override
	public int insertEmail(CustomerType type, String subject, String sender, String recipient, String description,
			LocalDateTime date, int customerID) throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO email(sender,recipient,subject, description,customer_id) VALUES(?, ?, ?, ?, ?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, sender);
		stmt.setString(2, recipient);
		stmt.setString(3, subject);
		stmt.setString(4, description);
		stmt.setInt(5, customerID);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertEmployee(String name, String surname, String phoneNumber, String email, String username,
			char[] password, boolean isManager) throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO employee (name, surname, username, password, phone_number, email, is_manager) VALUES(?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, name);
		stmt.setString(2, surname);
		stmt.setString(3, username);
		stmt.setString(4, String.valueOf(password));
		stmt.setString(5, phoneNumber);
		stmt.setString(6, email);
		stmt.setBoolean(7, isManager);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);

	}

	@Override
	public int insertMeeting(CustomerType type, String description, LocalDateTime date, int customerID)
			throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO meeting(description, date, %s) VALUES(?, ?, ?);", column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, description);
		stmt.setObject(2, date);
		stmt.setInt(3, customerID);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertNote(CustomerType type, int customerID, String title, String text)
			throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO note(%s, title, text) VALUES(?,?,?);", column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, customerID);
		stmt.setString(2, title);
		stmt.setString(3, text);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertOrder(CustomerType type, int customerID, LocalDateTime date, String description, float price,
			String productList) throws  SQLException,  SQLException {
		checkConnection();
		String column = CustomerType.CONTACT == type ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO `order`(%s, date, description, price, product_list) VALUES(?,?,?,?,?);",
				column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setInt(i++, customerID);
		stmt.setObject(i++, date);
		stmt.setString(i++, description);
		stmt.setFloat(i++, price);
		stmt.setString(i++, productList);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertPhoneCall(CustomerType type, String description, LocalDateTime date, int customerID)
			throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO phone_call(description, date, %s) VALUES(?, ?, ?);", column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, description);
		stmt.setObject(2, date);
		stmt.setInt(3, customerID);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertProduct(Product product) throws  SQLException {
		checkConnection();// TODO
		// cancellare
		// se non
		// serve
		String sql = "INSERT INTO product(product_code, name, price) VALUES(?,?,?);";
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, product.getProductCode());
		stmt.setString(2, product.getName());
		stmt.setDouble(3, product.getPrice());
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	@Override
	public int insertReminder(CustomerType type, int customerID, LocalDate dateTime, String description)
			throws  SQLException {
		checkConnection();
		String column = type == CustomerType.CONTACT ? "contact_id" : "company_id";
		String sql = String.format("INSERT INTO reminder(%s, date, description) VALUES(?,?,?);", column);
		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, customerID);
		stmt.setObject(2, dateTime);
		stmt.setString(3, description);
		stmt.executeUpdate();
		return getPrimaryKey(stmt);
	}

	private void performUpdateQuery(String table, String column, Object newValue, int id)
			throws  SQLException {
		checkConnection();
		String sql = String.format("UPDATE %s SET %s = ? WHERE id = ?;", table, column);
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setObject(1, newValue);
		stmt.setInt(2, id);
		stmt.execute();
		stmt.close();
	}

	@Override
	public void saveToHistory(HistoryType type, int employeeID, String message)
			throws  SQLException {
		checkConnection();
		String sql = "INSERT INTO employee_history(type, employee_id, description) VALUES(?, ?, ?);";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, type.toString());
		if (employeeID != 0)
			stmt.setInt(2, employeeID);
		else
			stmt.setNull(2, Types.INTEGER);
		stmt.setString(3, message);
		stmt.execute();
		stmt.close();
	}

	@Override
	public ArrayList<Company> searchCompanies(String searchQuery, int employeeID)
			throws  SQLException {
		checkConnection();
		String sql = "SELECT * FROM company WHERE (id LIKE ? OR name LIKE ? OR partita_iva LIKE ? "
				+ " OR email LIKE ? OR phone_number LIKE ? OR address LIKE ? OR business_state LIKE ?)";
		if (employeeID != 0)
			sql += " AND employee_id = ? ";
		sql += " GROUP BY id;";
		PreparedStatement stmt = connection.prepareStatement(sql);
		for (int i = 1; i <= 7; i++)
			stmt.setString(i, "%" + searchQuery + "%");
		if (employeeID != 0)
			stmt.setInt(8, employeeID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Company> list = new ArrayList<Company>();
		while (rs.next()) {
			int id = rs.getInt("id");
			BusinessState businessState = Enum.valueOf(BusinessState.class, rs.getString("business_state"));
			String address = rs.getString("address");
			String email = rs.getString("email");
			String phoneNumber = rs.getString("phone_number");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			String companyName = rs.getString("name");
			String partitaIva = rs.getString("partita_iva");
			list.add(new Company(id, contractStipulation, businessState, companyName, address, partitaIva, email,
					phoneNumber));
		}
		stmt.close();
		rs.close();
		return list;
	}

	@Override
	public ArrayList<Contact> searchContacts(String searchQuery, int employeeID)
			throws  SQLException {
		checkConnection();
		String sql = "SELECT * FROM contact WHERE (id LIKE ? OR first_name LIKE ? OR surname LIKE ? "
				+ " OR email LIKE ? OR phone_number LIKE ? OR address LIKE ? OR business_state LIKE ?)";
		if (employeeID != 0)
			sql += " AND employee_id = ? ";
		sql += " GROUP BY id;";// da fare per ogni colonna della tabella
		PreparedStatement stmt = connection.prepareStatement(sql);
		for (int i = 1; i <= 7; i++)
			stmt.setString(i, "%" + searchQuery + "%");
		if (employeeID != 0)
			stmt.setInt(8, employeeID);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Contact> list = new ArrayList<Contact>();
		while (rs.next()) {
			int id = rs.getInt("id");
			BusinessState businessState = Enum.valueOf(BusinessState.class, rs.getString("business_state"));
			String address = rs.getString("address");
			String email = rs.getString("email");
			String phoneNumber = rs.getString("phone_number");
			LocalDate contractStipulation = rs.getDate("contract_stipulation").toLocalDate();
			String firstName = rs.getString("first_name");
			String surname = rs.getString("surname");
			list.add(new Contact(firstName, surname, address, email, id, contractStipulation, businessState,
					phoneNumber));
		}
		stmt.close();
		rs.close();
		return list;
	}

	@Override
	public void updateCompany(int companyID, String toUpdate, Object newValue)
			throws  SQLException, CustomerNotFoundException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "partitaIva":
			toUpdate = "partita_iva";
			break;
		case "phoneNumber":
			toUpdate = "phone_number";
			break;
		case "contractStipulation":
			toUpdate = "contract_stipulation";
			break;
		case "businessState":
			toUpdate = "business_state";
			newValue = ((BusinessState) newValue).toString();
			break;
		case "employeeID":
			toUpdate = "employee_id";
			break;
		case "name":
		case "email":
		case "address":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("company", toUpdate, newValue, companyID);
	}

	@Override
	public void updateConferenceCall(int ID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "date":
		case "description":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("conference_call", toUpdate, newValue, ID);
	}

	@Override
	public void updateContact(int contactID, String toUpdate, Object newValue)
			throws  SQLException, CustomerNotFoundException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "businessState":
			toUpdate = "business_state";
			newValue = ((BusinessState) newValue).toString();
			break;
		case "firstName":
			toUpdate = "first_name";
			break;
		case "phoneNumber":
			toUpdate = "phone_number";
			break;
		case "companyID":
			toUpdate = "company_id";
			break;
		case "employeeID":
			toUpdate = "employee_id";
			break;
		case "contractStipulation":
			toUpdate = "contract_stipulation";
			break;
		case "surname":
		case "email":
		case "address":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("contact", toUpdate, newValue, contactID);
	}

	@Override
	public void updateCustomerListDetail(int listID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "list_description":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("customer_list_detail", toUpdate, newValue, listID);
	}

	@Override
	public void updateEmployee(int employeeID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "username":
		case "surname":
		case "email":
		case "address":
			break;
		case "firstName":
			toUpdate = "name";
			break;
		case "phoneNumber":
			toUpdate = "phone_number";
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("employee", toUpdate, newValue, employeeID);
	}

	@Override
	public void updateMeeting(int customerID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "description":
		case "date":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("meeting", toUpdate, newValue, customerID);
	}

	@Override
	public void updateNote(int noteID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "text":
		case "title":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("note", toUpdate, newValue, noteID);
	}

	@Override
	public void updateOrder(int orderID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "date":
		case "product_list":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("`order`", toUpdate, newValue, orderID);
	}

	@Override
	public void updateOrderDetails(int orderID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "product_id":
		case "amount":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("order_details", toUpdate, newValue, orderID);
	}

	@Override
	public void updatePhoneCall(int phoneCallID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "date":
		case "description":
		case "employee_id":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("phone_call", toUpdate, newValue, phoneCallID);
	}

	@Override
	public void updateProduct(int productID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "name":
		case "price":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("product", toUpdate, newValue, productID);
	}

	@Override
	public void updateReminder(int reminderID, String toUpdate, Object newValue)
			throws  SQLException, InexistingColumnException {
		checkConnection();
		switch (toUpdate) {
		case "date":
		case "description":
			break;
		default:
			throw new InexistingColumnException(toUpdate);
		}
		performUpdateQuery("reminder", toUpdate, newValue, reminderID);
	}

}
