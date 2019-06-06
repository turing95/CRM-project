package com.gruppo16.crm.client.gui.employee.panels;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.gruppo16.crm.client.core.CustomerListsHandler;
import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.Customer;
import com.gruppo16.crm.customers.CustomerList;
import com.gruppo16.crm.customers.CustomerListType;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.exceptions.CustomerNotFoundException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class CustomerListsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1070947662137273103L;
	private JList<CustomerList> customerListNameJList = new JList<CustomerList>();
	private DefaultListModel<CustomerList> customerListNameListModel = new DefaultListModel<CustomerList>();
	private DefaultListModel<Customer> customerListJListModel = new DefaultListModel<Customer>();
	private JButton btnInviaEmail;
	private JTextField customerListDescriptionTextField;
	private JButton customerListSaveButton;
	private JList<Customer> customerListJList = new JList<Customer>();
	private JComboBox<Object> listTypeComboBox;
	private CRMClient client = CRMClient.getInstance();
	private JFrame frame;
	private JTable contactTable;
	private JTable companyTable;
	private TableModel contactTableModel;
	private TableModel companyTableModel;

	public CustomerListsPanel(EmployeeWindow parent) {
		super();

		this.setLayout(null);

		frame = parent.getFrame();
		contactTable = parent.getContactTable();
		companyTable = parent.getCompanyTable();
		contactTableModel = contactTable.getModel();
		companyTableModel = companyTable.getModel();

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setBounds(12, 38, 315, 416);
		this.add(scrollPane_3);

		scrollPane_3.setViewportView(customerListNameJList);
		customerListNameJList.setModel(customerListNameListModel);

		customerListNameJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (customerListNameJList.isSelectionEmpty()) {
					btnInviaEmail.setVisible(false);
					customerListDescriptionTextField.setText("");
					customerListJListModel.clear();
					customerListSaveButton.setText("Salva nuova lista");
				} else {
					if (customerListNameJList.getSelectedValue().getListType() == CustomerListType.MAILING_LIST)
						btnInviaEmail.setVisible(true);
					else
						btnInviaEmail.setVisible(false);
					CustomerList customerList = customerListNameJList.getSelectedValue();
					customerListDescriptionTextField.setText(customerList.getDescription());
					customerListJListModel.clear();
					for (Customer customer : customerList.getList())
						customerListJListModel.addElement(customer);
					customerListSaveButton.setText("Salva modifiche");
				}
			}
		});

		JLabel lblDescrizione_4 = new JLabel("Descrizione:");
		lblDescrizione_4.setBounds(356, 40, 72, 16);
		this.add(lblDescrizione_4);

		customerListDescriptionTextField = new JTextField();
		customerListDescriptionTextField.setBounds(429, 37, 384, 22);
		this.add(customerListDescriptionTextField);
		customerListDescriptionTextField.setColumns(10);

		JLabel lblListaClienti = new JLabel("Lista clienti:");
		lblListaClienti.setBounds(356, 66, 72, 16);
		this.add(lblListaClienti);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_4.setBounds(429, 66, 384, 273);
		this.add(scrollPane_4);

		customerListJList.setModel(customerListJListModel);
		scrollPane_4.setViewportView(customerListJList);

		customerListSaveButton = new JButton("Salva");
		customerListSaveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String description = customerListDescriptionTextField.getText();
				CustomerListType listType = (CustomerListType) listTypeComboBox.getSelectedItem();

				ArrayList<Customer> list = new ArrayList<Customer>();
				for (int i = 0; i < customerListJListModel.getSize(); i++)
					list.add(customerListJListModel.get(i));

				try {
					if (!customerListJList.isSelectionEmpty()) {
						client.deleteCustomerList(customerListNameJList.getSelectedValue().getCustomerListID());
						customerListNameListModel.remove(customerListNameJList.getSelectedIndex());
					}
					int customerListID = client.saveNewCustomerList(description, listType, list);
					CustomerList customerList = new CustomerList(customerListID, description, list, listType);
					customerListNameListModel.addElement(customerList);
					customerListJListModel.clear();
					customerListDescriptionTextField.setText("");
					fillCustomerListNamesJList();
				} catch (RemoteException | ServerInternalErrorException 
						| UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});
		customerListSaveButton.setBounds(429, 402, 384, 52);
		this.add(customerListSaveButton);

		JButton btnElimina_1 = new JButton("Elimina");
		btnElimina_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.deleteCustomerList(customerListNameJList.getSelectedValue().getCustomerListID());
				} catch (RemoteException | ServerInternalErrorException 
						| UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
				customerListNameListModel.remove(customerListNameJList.getSelectedIndex());
			}
		});
		btnElimina_1.setBounds(12, 11, 97, 25);
		this.add(btnElimina_1);

		JButton btnAggiungiSelezionato = new JButton("Aggiungi");
		btnAggiungiSelezionato.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactTable.getSelectedRow() != -1) {
					int customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
					try {
						Customer customer = CustomerListsHandler.getCustomerByID(CustomerType.CONTACT,
								parent.getCustomerList(), customerID);
						if (!customerListJListModel.contains(customer))
							customerListJListModel.addElement(customer);
					} catch (CustomerNotFoundException e1) {
						e1.printStackTrace();
						GUIUtils.showCriticalError(e1, frame);
					}
				} else if (companyTable.getSelectedRow() != -1) {
					System.out.println("dsagasdkpkg");
					int customerID = (int) companyTableModel.getValueAt(companyTable.getSelectedRow(), 0);
					try {
						Customer customer = CustomerListsHandler.getCustomerByID(CustomerType.COMPANY,
								parent.getCustomerList(), customerID);
						if (!customerListJListModel.contains(customer))
							customerListJListModel.addElement(customer);
					} catch (CustomerNotFoundException e1) {
						e1.printStackTrace();
						GUIUtils.showCriticalError(e1, frame);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Selezionare prima un cliente dalla lista in alto!", "Errore!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnAggiungiSelezionato.setBounds(825, 64, 119, 25);
		this.add(btnAggiungiSelezionato);

		listTypeComboBox = new JComboBox<Object>();
		listTypeComboBox.setModel(new DefaultComboBoxModel<Object>(CustomerListType.values()));
		listTypeComboBox.setBounds(429, 367, 384, 22);
		this.add(listTypeComboBox);

		JButton btnRimuovi = new JButton("Rimuovi");
		btnRimuovi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (customerListJList.isSelectionEmpty())
					JOptionPane.showMessageDialog(frame, "Selezionare prima un cliente da cancellare dalla lista!",
							"Errore!", JOptionPane.ERROR_MESSAGE);
				else
					customerListJListModel.remove(customerListJList.getSelectedIndex());
			}
		});
		btnRimuovi.setBounds(825, 102, 119, 25);
		this.add(btnRimuovi);

		JLabel lblSelezionareUnContattoazienda = new JLabel(
				"Selezionare un contatto/azienda dalla rispettiva lista e cliccare su \"Aggiungi\" per aggiungerlo");
		lblSelezionareUnContattoazienda.setBounds(349, 11, 595, 16);
		this.add(lblSelezionareUnContattoazienda);

		btnInviaEmail = new JButton("Invia email...");
		btnInviaEmail.setVisible(false);
		btnInviaEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int max = customerListJListModel.getSize();
				if (max == 0)
					return;
				String message = "mailto:";
				for (int i = 0; i < max; i++)
					message += customerListJListModel.get(i).getEmail() + ";";
				Desktop desktop = Desktop.getDesktop();
				try {
					URI uri = URI.create(message);
					desktop.mail(uri);
				} catch (IOException | IllegalArgumentException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});
		btnInviaEmail.setBounds(825, 140, 119, 25);
		this.add(btnInviaEmail);

		JLabel lblScegliereIlTipo = new JLabel("Scegliere il tipo di lista:");
		lblScegliereIlTipo.setBounds(429, 345, 204, 16);
		this.add(lblScegliereIlTipo);

		JButton btnCreaNuova = new JButton("Crea nuova...");
		btnCreaNuova.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIUtils.emptyForms(customerListDescriptionTextField);
				customerListJListModel.clear();
				customerListDescriptionTextField.grabFocus();
			}
		});
		btnCreaNuova.setBounds(208, 11, 119, 25);
		add(btnCreaNuova);

	}

	public void fillCustomerListNamesJList() {
		customerListNameListModel.clear();
		try {
			for (CustomerList customerList : client.getCustomerLists()) {
				customerListNameListModel.addElement(customerList);
			}
		} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
			GUIUtils.showCriticalError(e, frame);
		}
	}

	public void fillCustomerListJList(ArrayList<Customer> list) {
		customerListJListModel = new DefaultListModel<>();
		for (Customer customer : list) {
			customerListJListModel.addElement(customer);
		}
		customerListJList.setModel(customerListJListModel);
	}

}
