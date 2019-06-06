package com.gruppo16.crm.client.gui.employee;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.panels.CustomerListsPanel;
import com.gruppo16.crm.client.gui.employee.panels.EventPanel;
import com.gruppo16.crm.client.gui.employee.panels.NotePanel;
import com.gruppo16.crm.client.gui.employee.panels.OrderPanel;
import com.gruppo16.crm.client.gui.employee.panels.ReminderPanel;
import com.gruppo16.crm.client.gui.manager.ManagerWindow;
import com.gruppo16.crm.client.gui.utils.DateLabelFormatter;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.BusinessState;
import com.gruppo16.crm.customers.Company;
import com.gruppo16.crm.customers.Contact;
import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.exceptions.NoLoggedUserException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class EmployeeWindow {

	private CRMClient client;
	private JFrame frame;
	private JTable contactTable;
	private JTable companiesTable = new JTable();
	private DefaultTableModel contactTableModel;
	private DefaultTableModel companyTableModel;
	private JMenuItem mntmLogout;
	private JTable companyTable = new JTable();
	private JTextField contactFirstNameTextField;
	private JTextField contactSurnameTextField;
	private JTextField contactAddressTextField;
	private JTextField companyNameTextField;
	private JTextField companyPartitaIvaTextField;
	private JTextField companyAddressTextField;
	private JTextField companyPhoneNumberTextField;
	private JTextField contactPhoneNumberTextField;
	private JTextField contactEmailTextField;
	private JTextField companyEmailTextField;

	/** Lista dei clienti gestiti dal dipendente che ha effettuato il login */
	private ArrayList<Customer> customerList = new ArrayList<Customer>();
	private ArrayList<Company> companies = new ArrayList<Company>();
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private JTextField searchTextField;
	private JComboBox<BusinessState> contactBusinessStateComboBox;
	private JDatePickerImpl contactStipulationDatePicker;
	private JComboBox<BusinessState> companyBusinessStateComboBox;
	private JDatePickerImpl companyStipulationDatePicker;
	private JTabbedPane customerListTabbedPane;
	private ReminderPanel reminderPanelNew;
	private NotePanel notePanel;
	private EventPanel eventPanel;
	private OrderPanel orderPanel;
	private CustomerListsPanel customerListsPanel;

	public ArrayList<Customer> getCustomerList() {
		return customerList;
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTable getContactTable() {
		return contactTable;
	}

	public JTable getCompanyTable() {
		return companyTable;
	}

	/**
	 * Launch the application.
	 */
	public static void mostra(CRMClient client) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					EmployeeWindow window = new EmployeeWindow(client);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EmployeeWindow(CRMClient client) {
		super();// Chiamo il costruttore di Observable
		this.client = client;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		GUIUtils.setLookAndFeel();
		frame = new JFrame();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		// frame.setResizable(false);
		// frame.setBounds(100, 100, 1499, 1117);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		frame.setLocationRelativeTo(null);// Metto la finestra al centro dello
											// schermo

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmEsci = new JMenuItem("Esci");
		mntmEsci.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					client.logout(frame);
				} catch (NoLoggedUserException | RemoteException | ServerInternalErrorException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});

		JMenuItem mntmGestioneDipendenti = new JMenuItem("Gestione dipendenti...");
		if (!client.getLoggedEmployee().isManager())
			mntmGestioneDipendenti.setEnabled(false);
		mntmGestioneDipendenti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				ManagerWindow.showWindow(client);
			}
		});
		mnFile.add(mntmGestioneDipendenti);

		JMenuItem mntmCambiaDatiPersonali = new JMenuItem("Modifica profilo");
		mntmCambiaDatiPersonali.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetailsEditorDialog.showMe();
			}
		});
		mnFile.add(mntmCambiaDatiPersonali);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmLogout);
		mnFile.add(mntmEsci);
		contactTableModel = new DefaultTableModel(null, new String[] { "ID", "Nome", "Cognome", "Email", "Telefono",
				"Indirizzo", "Business State", "Data stipulazione contratto" }) {
			private static final long serialVersionUID = -8639523591097211032L;
			Class[] columnTypes = new Class[] { Integer.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		companyTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Nome", "Indirizzo", "Email", "Business State", "Data inizio contratto" }) {
			private static final long serialVersionUID = 2424243816288244189L;
			Class[] columnTypes = new Class[] { Integer.class, String.class, String.class, String.class, String.class,
					String.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};

		companiesTable = new JTable();
		companiesTable.setModel(companyTableModel);
		frame.getContentPane().setLayout(null);

		customerListTabbedPane = new JTabbedPane(SwingConstants.TOP);
		customerListTabbedPane.setBounds(0, 65, 1902, 400);
		frame.getContentPane().add(customerListTabbedPane);

		JPanel contactPanel = new JPanel();
		customerListTabbedPane.addTab("Persone", null, contactPanel, null);
		contactPanel.setLayout(null);

		JScrollPane contactScrollPane = new JScrollPane();
		contactScrollPane.setBounds(0, 13, 1897, 365);
		contactPanel.add(contactScrollPane);
		contactScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		contactTable = new JTable();
		contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contactTable.setModel(contactTableModel);
		contactScrollPane.setViewportView(contactTable);

		// Evento di selezione riga delle tabelle
		contactTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (contactTable.getSelectedRow() != -1) {
					fillEveryForm();
					Contact contact;
					for (Customer customer : customerList) {
						if (customer instanceof Contact && customer.getCustomerID() == (int) contactTableModel
								.getValueAt(contactTable.getSelectedRow(), 0)) {
							contact = (Contact) customer;
							contactFirstNameTextField.setText(contact.getFirstName());
							contactSurnameTextField.setText(contact.getSurname());
							contactEmailTextField.setText(contact.getEmail());
							contactPhoneNumberTextField.setText(contact.getPhoneNumber());
							contactAddressTextField.setText(contact.getAddress());
							contactBusinessStateComboBox.setSelectedItem(contact.getState());
							contactStipulationDatePicker.getJFormattedTextField()
									.setText(contact.getContractStipulation().toString());
							break;
						}
					}

				}
			}
		});

		JPanel companyPanel = new JPanel();
		customerListTabbedPane.addTab("Aziende", null, companyPanel, null);
		companyPanel.setLayout(null);

		JScrollPane companyScrollPane = new JScrollPane();
		companyScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		companyScrollPane.setBounds(0, 13, 1885, 364);
		companyPanel.add(companyScrollPane);

		companyTable = new JTable();
		companyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (companyTable.getSelectedRow() != -1) {
					fillEveryForm();
					Company company;
					for (Customer customer : customerList) {
						if (customer instanceof Company && customer.getCustomerID() == (int) companyTableModel
								.getValueAt(companyTable.getSelectedRow(), 0)) {
							company = (Company) customer;
							companyNameTextField.setText(company.getName());
							companyEmailTextField.setText(company.getEmail());
							companyPhoneNumberTextField.setText(company.getPhoneNumber());
							companyAddressTextField.setText(company.getAddress());
							companyBusinessStateComboBox.setSelectedItem(company.getState());
							companyStipulationDatePicker.getJFormattedTextField()
									.setText(company.getContractStipulation().toString());
							break;
						}
					}
				}
			}
		});
		companyTable.setModel(companyTableModel);
		companyScrollPane.setViewportView(companyTable);

		JPanel searchPanel = new JPanel();
		searchPanel.setBounds(0, 0, 1914, 61);
		frame.getContentPane().add(searchPanel);
		searchPanel.setLayout(null);

		JLabel lblFiltraPer = new JLabel("Filtra per:");
		lblFiltraPer.setBounds(0, 0, 64, 16);
		searchPanel.add(lblFiltraPer);

		JCheckBox chckbxSoloAssegnatiA = new JCheckBox("Solo assegnati a " + client.getLoggedEmployee().getName());
		chckbxSoloAssegnatiA.setSelected(true);
		chckbxSoloAssegnatiA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCustomerTables(chckbxSoloAssegnatiA.isSelected());
			}
		});

		chckbxSoloAssegnatiA.setBounds(65, -4, 242, 25);
		searchPanel.add(chckbxSoloAssegnatiA);

		searchTextField = new JTextField();
		searchTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (searchTextField.getForeground().equals(Color.GRAY)) {
					searchTextField.setText("");
					searchTextField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (searchTextField.getText().isEmpty()) {
					searchTextField.setForeground(Color.GRAY);
					searchTextField.setText("Cerca...");
				}
			}
		});
		searchTextField.setForeground(Color.GRAY);
		searchTextField.setText("Cerca...");
		searchTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!searchTextField.getText().isEmpty())
					performSearch();
				else
					showCustomerTables(chckbxSoloAssegnatiA.isSelected());

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (searchTextField.hasFocus())
					performSearch();

			}

			private void performSearch() {
				String searchQuery = searchTextField.getText();
				if (searchQuery.isEmpty())
					return;
				int employeeID;
				if (chckbxSoloAssegnatiA.isSelected())
					employeeID = client.getLoggedEmployee().getEmployeeID();
				else
					employeeID = 0;
				if (customerListTabbedPane.getSelectedComponent().equals(contactPanel)) {
					try {
						updateContactTable(client.searchContact(searchQuery, employeeID));
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, frame);
					}
				} else {
					try {
						updateCompanyTable(client.searchCompany(searchQuery, employeeID));
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, frame);
					}
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		searchTextField.setBounds(0, 26, 318, 35);
		searchPanel.add(searchTextField);
		searchTextField.setColumns(10);

		JPanel eventsPanel = new JPanel();
		eventsPanel.setBounds(0, 478, 1022, 497);
		frame.getContentPane().add(eventsPanel);
		eventsPanel.setLayout(null);

		JTabbedPane customerDetailsTabbedPane = new JTabbedPane(SwingConstants.TOP);
		customerDetailsTabbedPane.setBounds(0, 0, 1022, 497);
		eventsPanel.add(customerDetailsTabbedPane);

		reminderPanelNew = new ReminderPanel(this);
		customerDetailsTabbedPane.addTab("Promemoria", null, reminderPanelNew, null);

		notePanel = new NotePanel(this);
		customerDetailsTabbedPane.addTab("Note", null, notePanel, null);

		eventPanel = new EventPanel(this);
		customerDetailsTabbedPane.addTab("Evento", null, eventPanel, null);

		orderPanel = new OrderPanel(this);
		customerDetailsTabbedPane.addTab("Ordini", null, orderPanel, null);

		customerListsPanel = new CustomerListsPanel(this);
		customerDetailsTabbedPane.addTab("Liste/Gruppi/Mailing list", null, customerListsPanel, null);

		JTabbedPane customerFormsTabbedPane = new JTabbedPane(SwingConstants.TOP);
		customerFormsTabbedPane.setBounds(1034, 486, 868, 476);
		frame.getContentPane().add(customerFormsTabbedPane);

		JPanel contactFormPanel = new JPanel();
		customerFormsTabbedPane.addTab("Persona", null, contactFormPanel, null);
		contactFormPanel.setLayout(null);

		JLabel lblNome = new JLabel("Nome:");
		lblNome.setBounds(119, 73, 38, 16);
		contactFormPanel.add(lblNome);

		contactFirstNameTextField = new JTextField();
		contactFirstNameTextField.setBounds(164, 70, 555, 22);
		contactFormPanel.add(contactFirstNameTextField);
		contactFirstNameTextField.setColumns(10);

		JLabel lblCognome = new JLabel("Cognome:");
		lblCognome.setBounds(98, 102, 59, 16);
		contactFormPanel.add(lblCognome);

		contactSurnameTextField = new JTextField();
		contactSurnameTextField.setBounds(164, 99, 555, 22);
		contactFormPanel.add(contactSurnameTextField);
		contactSurnameTextField.setColumns(10);

		JLabel lblIndirizzo = new JLabel("Indirizzo:");
		lblIndirizzo.setBounds(104, 131, 53, 16);
		contactFormPanel.add(lblIndirizzo);

		contactAddressTextField = new JTextField();
		contactAddressTextField.setBounds(164, 128, 555, 22);
		contactFormPanel.add(contactAddressTextField);
		contactAddressTextField.setColumns(10);

		JLabel lblBusinessState = new JLabel("Business State:");
		lblBusinessState.setBounds(69, 160, 88, 16);
		contactFormPanel.add(lblBusinessState);

		contactBusinessStateComboBox = new JComboBox();
		contactBusinessStateComboBox.setBounds(164, 157, 555, 22);
		contactBusinessStateComboBox.setModel(new DefaultComboBoxModel(BusinessState.values()));
		contactFormPanel.add(contactBusinessStateComboBox);

		JLabel lblTelefono = new JLabel("Telefono:");
		lblTelefono.setBounds(102, 195, 55, 16);
		contactFormPanel.add(lblTelefono);

		contactPhoneNumberTextField = new JTextField();
		contactPhoneNumberTextField.setBounds(164, 192, 555, 22);
		contactFormPanel.add(contactPhoneNumberTextField);
		contactPhoneNumberTextField.setColumns(10);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(121, 224, 36, 16);
		contactFormPanel.add(lblEmail);

		contactEmailTextField = new JTextField();
		contactEmailTextField.setBounds(164, 221, 555, 22);
		contactFormPanel.add(contactEmailTextField);
		contactEmailTextField.setColumns(10);

		JLabel lblDataStipulazioneContratto = new JLabel("Data stipulazione contratto:");
		lblDataStipulazioneContratto.setBounds(0, 265, 157, 16);
		contactFormPanel.add(lblDataStipulazioneContratto);

		UtilDateModel stipulationDateModel = new UtilDateModel();
		JDatePanelImpl stipulationDatePanel = new JDatePanelImpl(stipulationDateModel, new Properties());
		contactStipulationDatePicker = new JDatePickerImpl(stipulationDatePanel, new DateLabelFormatter());
		contactStipulationDatePicker.setBounds(164, 256, 555, 25);
		contactFormPanel.add(contactStipulationDatePicker);
		JButton btnSalva_6 = new JButton("Salva modifiche");
		btnSalva_6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveContact();
			}
		});
		btnSalva_6.setBounds(562, 302, 157, 60);
		contactFormPanel.add(btnSalva_6);

		JButton btnCreaNuovo_1 = new JButton("Crea nuovo...");
		btnCreaNuovo_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contactTable.clearSelection();
				GUIUtils.emptyForms(contactFirstNameTextField, contactSurnameTextField, contactAddressTextField,
						contactEmailTextField, contactPhoneNumberTextField);
				contactBusinessStateComboBox.setSelectedIndex(0);
				contactFirstNameTextField.grabFocus();
			}
		});
		btnCreaNuovo_1.setBounds(12, 13, 116, 25);
		contactFormPanel.add(btnCreaNuovo_1);

		JButton btnNewButton = new JButton("Elimina selezionato");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (contactTable.getSelectedRow() != -1) {
					int customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
					try {
						client.deleteCustomer(CustomerType.CONTACT, customerID);
						contactTableModel.removeRow(contactTable.getSelectedRow());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, frame);
					}

				}

			}
		});
		btnNewButton.setBounds(140, 13, 157, 25);
		contactFormPanel.add(btnNewButton);

		JPanel companyFormPanel = new JPanel();
		customerFormsTabbedPane.addTab("Azienda", null, companyFormPanel, null);
		companyFormPanel.setLayout(null);

		JLabel lblNome_1 = new JLabel("Nome:");
		lblNome_1.setBounds(76, 41, 38, 16);
		companyFormPanel.add(lblNome_1);

		companyNameTextField = new JTextField();
		companyNameTextField.setBounds(117, 38, 742, 22);
		companyFormPanel.add(companyNameTextField);
		companyNameTextField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Partita IVA:");
		lblNewLabel_3.setBounds(48, 70, 66, 16);
		companyFormPanel.add(lblNewLabel_3);

		companyPartitaIvaTextField = new JTextField();
		companyPartitaIvaTextField.setBounds(117, 67, 742, 22);
		companyFormPanel.add(companyPartitaIvaTextField);
		companyPartitaIvaTextField.setColumns(10);

		JLabel lblIndirizzo_1 = new JLabel("Indirizzo:");
		lblIndirizzo_1.setBounds(61, 99, 53, 16);
		companyFormPanel.add(lblIndirizzo_1);

		companyAddressTextField = new JTextField();
		companyAddressTextField.setBounds(117, 96, 742, 22);
		companyFormPanel.add(companyAddressTextField);
		companyAddressTextField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Telefono:");
		lblNewLabel_4.setBounds(59, 128, 55, 16);
		companyFormPanel.add(lblNewLabel_4);

		companyPhoneNumberTextField = new JTextField();
		companyPhoneNumberTextField.setBounds(117, 125, 742, 22);
		companyFormPanel.add(companyPhoneNumberTextField);
		companyPhoneNumberTextField.setColumns(10);

		JLabel lblBusinessState_1 = new JLabel("Business State:");
		lblBusinessState_1.setBounds(26, 157, 88, 16);
		companyFormPanel.add(lblBusinessState_1);

		companyBusinessStateComboBox = new JComboBox();
		companyBusinessStateComboBox.setBounds(117, 154, 742, 22);
		companyBusinessStateComboBox.setModel(new DefaultComboBoxModel(BusinessState.values()));
		companyFormPanel.add(companyBusinessStateComboBox);

		JLabel lblEmail_1 = new JLabel("Email:");
		lblEmail_1.setBounds(76, 186, 36, 16);
		companyFormPanel.add(lblEmail_1);

		companyEmailTextField = new JTextField();
		companyEmailTextField.setBounds(117, 183, 742, 22);
		companyFormPanel.add(companyEmailTextField);
		companyEmailTextField.setColumns(10);

		UtilDateModel companyStipulationDateModel = new UtilDateModel();
		JDatePanelImpl companyStipulationDatePanel = new JDatePanelImpl(companyStipulationDateModel, new Properties());

		JLabel lblDataStipulazione = new JLabel("Data stipulazione:");
		lblDataStipulazione.setBounds(12, 223, 102, 16);
		companyFormPanel.add(lblDataStipulazione);
		companyStipulationDatePicker = new JDatePickerImpl(companyStipulationDatePanel, new DateLabelFormatter());
		companyStipulationDatePicker.setBounds(117, 215, 742, 37);
		companyFormPanel.add(companyStipulationDatePicker);

		JButton btnNewButton_1 = new JButton("Salva modifiche");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCompany();
			}
		});
		btnNewButton_1.setBounds(712, 299, 147, 52);
		companyFormPanel.add(btnNewButton_1);

		JButton btnCreaNuova = new JButton("Crea nuova...");
		btnCreaNuova.setBounds(12, 3, 114, 25);
		companyFormPanel.add(btnCreaNuova);

		JButton btnNewButton_4 = new JButton("Elimina selezionato");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (companyTable.getSelectedRow() != -1) {
					int customerID = (int) companyTableModel.getValueAt(companyTable.getSelectedRow(), 0);
					try {
						client.deleteCustomer(CustomerType.CONTACT, customerID);
						companyTableModel.removeRow(companyTable.getSelectedRow());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, frame);
					}

				}
			}
		});
		btnNewButton_4.setBounds(137, 3, 147, 25);
		companyFormPanel.add(btnNewButton_4);

		customerListTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {// Quando cambio il
				// pannello di editing
				// cambia anche il
				// pannello delle liste
				contactTable.clearSelection();
				companyTable.clearSelection();
				customerFormsTabbedPane.setSelectedIndex(customerListTabbedPane.getSelectedIndex());
				if (customerListTabbedPane.getSelectedComponent().equals(contactPanel)) {
					System.out.println("contatto");
				} else {
					System.out.println("azienda");
					eventsPanel.setEnabled(false);
				}
			}
		});

		customerFormsTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {// Quando cambio il
														// pannello di editing
														// cambia anche il
														// pannello delle liste
				contactTable.clearSelection();
				companyTable.clearSelection();
				customerListTabbedPane.setSelectedIndex(customerFormsTabbedPane.getSelectedIndex());
			}
		});

		showCustomerTables(true);

	}

	public void showCustomerTables(boolean onlyAssigned) {
		customerListsPanel.fillCustomerListNamesJList();
		GUIUtils.resetTables(contactTableModel, companyTableModel);
		int employeeID = (onlyAssigned) ? client.getLoggedEmployee().getEmployeeID() : 0;
		try {
			contacts = client.getContacts(employeeID);
			companies = client.getCompanies(employeeID);
			for (Contact contact : contacts) {
				addCustomerToList(contact);
			}
			for (Company company : companies) {
				addCustomerToList(company);
			}
		} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
			GUIUtils.showCriticalError(e, frame);
		}

	}

	public void addCustomerToList(Customer customer) {
		if (customer instanceof Contact) {
			Contact contact = (Contact) customer;
			contactTableModel.addRow(new Object[] { contact.getCustomerID(), contact.getFirstName(),
					contact.getSurname(), contact.getEmail(), contact.getPhoneNumber(), contact.getAddress(),
					contact.getState().toString(), contact.getContractStipulation() });
		} else if (customer instanceof Company) {
			Company company = (Company) customer;
			companyTableModel.addRow(new Object[] { company.getCustomerID(), company.getName(), company.getAddress(),
					company.getEmail(), company.getState(), company.getContractStipulation().toString() });
		}
	}

	public void updateContactTable(ArrayList<Contact> list) {
		GUIUtils.resetTables(contactTableModel);
		for (Contact contact : list) {
			addCustomerToList(contact);
		}
	}

	public void updateCompanyTable(ArrayList<Company> list) {
		GUIUtils.resetTables(companyTableModel);
		for (Company company : list) {
			addCustomerToList(company);
		}
	}

	/**
	 * Chiamata quando viene selezionata una riga della tabella cliente, mostra
	 * nei form relativi ad esso tutti i dati, recuperati dal server
	 * 
	 * @throws ServerInternalErrorException
	 * @throws RemoteException
	 */
	public void fillEveryForm() {
		int customerID;
		CustomerType type = customerListTabbedPane.getSelectedIndex() == 0 ? CustomerType.CONTACT
				: CustomerType.COMPANY;
		if (customerListTabbedPane.getSelectedIndex() == 0)
			customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
		else if (customerListTabbedPane.getSelectedIndex() == 1)
			customerID = (int) companyTableModel.getValueAt(companyTable.getSelectedRow(), 0);
		else
			return;
		try {
			reminderPanelNew.updateTable(client.getReminders(type, customerID));
			notePanel.updateList(client.getNotes(type, customerID));
			eventPanel.updateList(client.getEvents(type, customerID));
			orderPanel.updateList(client.getOrders(type, customerID));
		} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
			GUIUtils.showCriticalError(e, frame);
		}
		// fillPastCompaniesList(client.);
	}

	private void fillPastCompaniesList(ArrayList<Company> pastCompanies) {
		DefaultListModel<Company> pastCompaniesModel = new DefaultListModel<>();
		for (Company company : pastCompanies)
			pastCompaniesModel.addElement(company);
	}

	public void saveContact() {
		if (!GUIUtils.isEverythingFilled(contactFirstNameTextField, contactSurnameTextField, contactAddressTextField,
				contactPhoneNumberTextField, contactEmailTextField,
				contactStipulationDatePicker.getJFormattedTextField())) {
			JOptionPane.showMessageDialog(frame, "Compilare tutti i campi richiesti!", "Errore!",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String firstName = contactFirstNameTextField.getText();
		String surname = contactSurnameTextField.getText();
		String address = contactAddressTextField.getText();
		BusinessState businessState = (BusinessState) contactBusinessStateComboBox.getSelectedItem();
		String phoneNumber = contactPhoneNumberTextField.getText();
		String email = contactEmailTextField.getText();
		LocalDate contractStipulation = LocalDate
				.parse(contactStipulationDatePicker.getJFormattedTextField().getText());

		if (contactTable.getSelectedRow() == -1) {
			try {
				client.saveNewContact(firstName, surname, address, email, contractStipulation, LocalDate.now(),
						businessState, phoneNumber);
			} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, frame);
			}
		} else {
			try {
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"firstName", firstName);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"surname", surname);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"address", address);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"businessState", businessState);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"phoneNumber", phoneNumber);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"email", email);
				client.updateContactValue(
						Integer.valueOf(String.valueOf(contactTable.getValueAt(contactTable.getSelectedRow(), 0))),
						"contractStipulation", contractStipulation);
			} catch (NumberFormatException | RemoteException | ServerInternalErrorException
					| UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, frame);
			}
		}
		showCustomerTables(true);
	}

	public void saveCompany() {
		if (!GUIUtils.isEverythingFilled(companyNameTextField, companyAddressTextField, companyPhoneNumberTextField,
				companyEmailTextField, companyStipulationDatePicker.getJFormattedTextField())) {
			JOptionPane.showMessageDialog(frame, "Compilare tutti i campi richiesti!", "Errore!",
					JOptionPane.ERROR_MESSAGE);
		}
		String companyName = companyNameTextField.getText();
		String address = companyAddressTextField.getText();
		String partitaIva = companyPartitaIvaTextField.getText();
		BusinessState businessState = (BusinessState) companyBusinessStateComboBox.getSelectedItem();
		String phoneNumber = companyPhoneNumberTextField.getText();
		String email = companyEmailTextField.getText();
		LocalDate contractStipulation = LocalDate
				.parse(companyStipulationDatePicker.getJFormattedTextField().getText());

		if (companyTable.getSelectedRow() == -1) {
			try {
				client.saveNewCompany(companyName, partitaIva, email, phoneNumber, address, contractStipulation,
						businessState);
			} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, frame);
				e.printStackTrace();
			}
		} else {
			try {
				client.updateCompanyValue(
						Integer.valueOf(String.valueOf(companyTable.getValueAt(companyTable.getSelectedRow(), 0))),
						"address", address);
				client.updateCompanyValue(
						Integer.valueOf(String.valueOf(companyTable.getValueAt(companyTable.getSelectedRow(), 0))),
						"businessState", businessState);
				client.updateCompanyValue(
						Integer.valueOf(String.valueOf(companyTable.getValueAt(companyTable.getSelectedRow(), 0))),
						"phoneNumber", phoneNumber);
				client.updateCompanyValue(
						Integer.valueOf(String.valueOf(companyTable.getValueAt(companyTable.getSelectedRow(), 0))),
						"email", email);
				client.updateCompanyValue(
						Integer.valueOf(String.valueOf(companyTable.getValueAt(companyTable.getSelectedRow(), 0))),
						"contractStipulation", contractStipulation);
			} catch (NumberFormatException | RemoteException | ServerInternalErrorException
					| UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, frame);
			}
		}
		showCustomerTables(true);
	}
}
