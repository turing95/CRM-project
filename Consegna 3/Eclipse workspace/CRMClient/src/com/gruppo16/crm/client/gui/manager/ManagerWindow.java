package com.gruppo16.crm.client.gui.manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.NoLoggedUserException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.history.HistoryEntry;

public class ManagerWindow {

	private CRMClient client;

	private JFrame frame;
	private JTable employeeTable;
	private JTable historyTable;
	private JTextField nameTextField;
	private JTextField surnameTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JTextField phoneTextField;
	private JTextField emailTextField;
	private DefaultTableModel employeeTableModel;

	private DefaultTableModel historyTableModel;

	/**
	 * Launch the application.
	 */
	public static void showWindow(CRMClient client) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManagerWindow window = new ManagerWindow(client);
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
	public ManagerWindow(CRMClient client) {
		this.client = client;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}

		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 929, 593);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 39, 915, 505);
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane, BorderLayout.CENTER);

		JPanel employeePanel = new JPanel();
		tabbedPane.addTab("Gestione Dipendenti", null, employeePanel, null);
		employeePanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(12, 13, 886, 237);
		employeePanel.add(scrollPane);

		employeeTable = new JTable();
		employeeTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Nome", "Username", "Numero di telefono", "Email", "Tipo" }) {
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
		employeeTable.setModel(employeeTableModel);
		scrollPane.setViewportView(employeeTable);

		employeeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO finire
			}
		});

		JPanel formPanel = new JPanel();
		formPanel.setBounds(12, 251, 886, 211);
		employeePanel.add(formPanel);
		formPanel.setLayout(null);

		nameTextField = new JTextField();
		nameTextField.setBounds(128, 29, 151, 20);
		formPanel.add(nameTextField);
		nameTextField.setColumns(10);

		surnameTextField = new JTextField();
		surnameTextField.setColumns(10);
		surnameTextField.setBounds(128, 62, 151, 20);
		formPanel.add(surnameTextField);

		usernameTextField = new JTextField();
		usernameTextField.setColumns(10);
		usernameTextField.setBounds(128, 95, 151, 20);
		formPanel.add(usernameTextField);

		JLabel lblNome = new JLabel("Nome");
		lblNome.setBounds(12, 25, 84, 28);
		formPanel.add(lblNome);

		JLabel lblCognome = new JLabel("Cognome");
		lblCognome.setBounds(12, 56, 84, 28);
		formPanel.add(lblCognome);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(12, 91, 84, 28);
		formPanel.add(lblUsername);

		JLabel lblP = new JLabel("Password");
		lblP.setBounds(12, 122, 84, 28);
		formPanel.add(lblP);

		passwordField = new JPasswordField();
		passwordField.setBounds(128, 126, 151, 20);
		formPanel.add(passwordField);

		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setBounds(128, 160, 151, 20);
		formPanel.add(confirmPasswordField);

		JLabel lblConfermaPassword = new JLabel("Conferma password");
		lblConfermaPassword.setBounds(12, 156, 126, 28);
		formPanel.add(lblConfermaPassword);

		phoneTextField = new JTextField();
		phoneTextField.setBounds(530, 13, 266, 20);
		formPanel.add(phoneTextField);
		phoneTextField.setColumns(10);

		emailTextField = new JTextField();
		emailTextField.setColumns(10);
		emailTextField.setBounds(530, 46, 266, 20);
		formPanel.add(emailTextField);

		JLabel lblTelefono = new JLabel("Telefono");
		lblTelefono.setBounds(451, 9, 67, 28);
		formPanel.add(lblTelefono);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(451, 42, 67, 28);
		formPanel.add(lblEmail);

		JCheckBox managerCheckBox = new JCheckBox("Manager");
		managerCheckBox.setBounds(530, 83, 266, 23);
		formPanel.add(managerCheckBox);

		JButton btnElimina = new JButton("Disattiva profilo");
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnElimina.setBounds(0, 0, 171, 23);
		formPanel.add(btnElimina);

		JButton btnSalva = new JButton("Salva");
		btnSalva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameTextField.getText();
				String surname = surnameTextField.getText();
				char[] password = passwordField.getPassword();
				String email = emailTextField.getText();
				String username = usernameTextField.getText();
				String phoneNumber = phoneTextField.getText();
				boolean isManager = managerCheckBox.isSelected();
				try {					
					client.saveNewEmployee(name, surname, username, password, email, phoneNumber, isManager);
					fillTables();
				} catch (RemoteException | ServerInternalErrorException 
						| UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});
		btnSalva.setBounds(531, 124, 266, 60);
		formPanel.add(btnSalva);
		formPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { lblTelefono, lblEmail, phoneTextField, emailTextField, nameTextField, lblNome,
						managerCheckBox, btnElimina, surnameTextField, usernameTextField, passwordField,
						confirmPasswordField, lblCognome, lblUsername, lblP, lblConfermaPassword, btnSalva }));

		JPanel logPanel = new JPanel();
		tabbedPane.addTab("Cronologia delle modifiche", null, logPanel, null);
		logPanel.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(12, 0, 900, 475);
		logPanel.add(scrollPane_1);

		historyTable = new JTable();
		historyTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Tipo", "Impiegato", "Descrizione", "Data" }) {
			private static final long serialVersionUID = 2424243816288244189L;
			Class[] columnTypes = new Class[] { Integer.class, String.class, String.class, String.class, String.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, false, false, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		historyTable.setModel(historyTableModel);
		scrollPane_1.setViewportView(historyTable);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 911, 26);
		frame.getContentPane().add(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNewMenuItem = new JMenuItem("Torna alla vista clienti...");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				EmployeeWindow.mostra(client);
			}
		});
		mnFile.add(mntmNewMenuItem);

		JMenuItem mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					client.logout(frame);							
				} catch (NoLoggedUserException | RemoteException | ServerInternalErrorException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});
		mnFile.add(mntmLogout);

		JMenuItem mntmEsci = new JMenuItem("Esci");
		mntmEsci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmEsci);
		fillTables();
	}

	public void fillTables() {
		GUIUtils.resetTables(employeeTableModel);
		try {
			for (Employee employee : client.getEmployeeList()) {
				String type = employee.isManager() ? "Manager" : "Dipendente";
				employeeTableModel.addRow(new Object[] { employee.getEmployeeID(), employee.getName(),
						employee.getUsername(), employee.getPhoneNumber(), employee.getEmail(), type });
			}
		} catch (RemoteException | UnathorizedUserException | ServerInternalErrorException
				 e1) {
			GUIUtils.showCriticalError(e1, frame);
		}
		try {
			for (HistoryEntry entry : client.getHistory()) {
				historyTableModel.addRow(new Object[] { entry.getHistoryID(), entry.getType().toString(),
						entry.getEmployeeID(), entry.getDescription(), entry.getDate().toString() });
			}
		} catch (RemoteException | ServerInternalErrorException 
				| UnathorizedUserException e) {
			GUIUtils.showCriticalError(e, frame);
		}
	}

}
