package com.gruppo16.crm.client.gui.employee.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.table.TableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.exceptions.EmptyFormsException;
import com.gruppo16.crm.client.gui.utils.DateLabelFormatter;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class OrderPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7885635240964711804L;
	private JFrame frame;
	private JTable contactTable;
	private JTable companyTable;
	private TableModel contactTableModel;
	private TableModel companyTableModel;
	private JList<Order> orderList = new JList<Order>();
	private DefaultListModel<Order> orderListModel = new DefaultListModel<Order>();
	private JTextField orderDescriptionTextField;
	private JDatePickerImpl orderDatePicker;
	private DateEditor de_orderTimeSpinner;
	private JTextArea orderTextArea;
	private JTextField orderPriceTextField;

	public OrderPanel(EmployeeWindow parent) {
		super();

		this.setLayout(null);

		frame = parent.getFrame();
		contactTable = parent.getContactTable();
		companyTable = parent.getCompanyTable();
		contactTableModel = contactTable.getModel();
		companyTableModel = companyTable.getModel();

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(12, 45, 343, 409);
		this.add(scrollPane_1);

		scrollPane_1.setViewportView(orderList);
		orderList.setModel(orderListModel);

		JLabel lblDescrizione_3 = new JLabel("Descrizione:");
		lblDescrizione_3.setBounds(367, 29, 78, 16);
		this.add(lblDescrizione_3);

		orderDescriptionTextField = new JTextField();
		orderDescriptionTextField.setBounds(447, 26, 484, 22);
		this.add(orderDescriptionTextField);
		orderDescriptionTextField.setColumns(10);

		JLabel lblDataEOra_1 = new JLabel("Data e ora:");
		lblDataEOra_1.setBounds(367, 61, 84, 16);
		this.add(lblDataEOra_1);

		UtilDateModel quoteUtilDateModel = new UtilDateModel();
		JDatePanelImpl quoteDatePanel = new JDatePanelImpl(quoteUtilDateModel, new Properties());
		orderDatePicker = new JDatePickerImpl(quoteDatePanel, new DateLabelFormatter());
		orderDatePicker.setBounds(447, 58, 426, 22);

		this.add(orderDatePicker);

		JSpinner orderTimeSpinner = new JSpinner(new SpinnerDateModel());
		de_orderTimeSpinner = new JSpinner.DateEditor(orderTimeSpinner, "HH:mm");
		orderTimeSpinner.setEditor(de_orderTimeSpinner);
		orderTimeSpinner.setValue(new Date()); // will only show the current
												// time
		orderTimeSpinner.setBounds(875, 58, 56, 22);

		this.add(orderTimeSpinner);

		JButton orderSaveButton = new JButton("Salva");
		orderSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOrder();
			}
		});
		orderSaveButton.setBounds(746, 353, 185, 60);
		this.add(orderSaveButton);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(447, 90, 484, 243);
		this.add(scrollPane_5);

		orderTextArea = new JTextArea();
		scrollPane_5.setViewportView(orderTextArea);

		JLabel lblListaProdotti = new JLabel("Lista prodotti:");
		lblListaProdotti.setBounds(361, 90, 84, 16);
		this.add(lblListaProdotti);

		orderPriceTextField = new JTextField();
		orderPriceTextField.setBounds(447, 349, 126, 22);
		this.add(orderPriceTextField);
		orderPriceTextField.setColumns(10);

		JLabel lblPrezzoTotale = new JLabel("Prezzo totale:");
		lblPrezzoTotale.setBounds(361, 352, 84, 16);
		this.add(lblPrezzoTotale);

		JLabel label = new JLabel("€");
		label.setBounds(581, 352, 56, 16);
		this.add(label);

		JButton orderDeleteButton = new JButton("Elimina");
		orderDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO implementare
			}
		});
		orderDeleteButton.setBounds(12, 13, 97, 25);
		this.add(orderDeleteButton);
	}

	public void saveOrder() {
		if (orderList.isSelectionEmpty()) {
			if (contactTable.getSelectedRow() != -1 || companyTable.getSelectedRow() != -1)
				try {
					int customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
					if (!GUIUtils.isEverythingFilled(orderDescriptionTextField,
							orderDatePicker.getJFormattedTextField(), orderTextArea, orderPriceTextField))
						throw new EmptyFormsException();
					CustomerType type = contactTable.getSelectedRow() != -1 ? CustomerType.CONTACT
							: CustomerType.COMPANY;
					LocalDateTime date = LocalDateTime.parse(orderDatePicker.getJFormattedTextField().getText() + "T"
							+ de_orderTimeSpinner.getTextField().getText());
					String description = orderDescriptionTextField.getText();
					String productList = orderTextArea.getText();
					float totalPrice = Float.valueOf(orderPriceTextField.getText());
					int eventID = CRMClient.getInstance().saveNewOrder(type, description, date, customerID, productList,
							totalPrice);
					Order order = new Order(eventID, customerID, description, date, totalPrice, productList);
					orderListModel.addElement(order);
					JOptionPane.showMessageDialog(frame, "Ordine salvato!", "Informazione!",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (EmptyFormsException | RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
					JOptionPane.showMessageDialog(frame, "Impossibile salvare l'ordine!\n" + e1.getMessage(), "Errore!",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(frame, "Formato prezzo non corretto!", "Errore!",
							JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(frame, "Tipo di evento non selezionato!", "Errore!",
							JOptionPane.ERROR_MESSAGE);
				}

		} else {
			Order order = orderList.getSelectedValue();
			// client.updateEventValue(event, "description",
			// noteTitleTextField.getText());
			JOptionPane.showMessageDialog(frame, "Ordine salvato!", "Informazione!", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public void updateList(ArrayList<Order> orders) {
		DefaultListModel<Order> orderListModel = new DefaultListModel<>();
		for (Order order : orders)
			orderListModel.addElement(order);
		orderList.setModel(orderListModel);
	}

}
