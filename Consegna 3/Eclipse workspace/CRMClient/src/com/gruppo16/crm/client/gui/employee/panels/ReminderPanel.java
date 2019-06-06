package com.gruppo16.crm.client.gui.employee.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.utils.DateLabelFormatter;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.events.Reminder;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class ReminderPanel extends JPanel {

	private static final long serialVersionUID = -1921627814504823760L;	
	private JList<Reminder> reminderList = new JList<Reminder>();
	private UtilDateModel reminderUtilDateModel;
	private JDatePanelImpl reminderDatePanel;
	private JDatePickerImpl reminderDatePicker;
	private JTextField reminderDescriptionTextField;
	private AbstractButton deleteReminderButton;
	private AbstractButton reminderSaveButton;
	private DefaultListModel<Reminder> reminderListModel = new DefaultListModel<Reminder>();
	private AbstractButton reminderAddButton;
	private CRMClient client = CRMClient.getInstance();
	private EmployeeWindow parent;

	public ReminderPanel(EmployeeWindow parent) {
		super();		
		this.parent = parent;
		this.setLayout(null);
		

		TableModel contactTableModel = parent.getContactTable().getModel();
		TableModel companyTableModel = parent.getCompanyTable().getModel();
		
		JScrollPane reminderListScrollPane = new JScrollPane();
		reminderListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		reminderListScrollPane.setBounds(12, 42, 404, 412);
		this.add(reminderListScrollPane);
		
		reminderList.setModel(reminderListModel);
				
		reminderListScrollPane.setViewportView(reminderList);

		JPanel reminderModPanel = new JPanel();
		reminderModPanel.setBounds(428, 13, 589, 441);
		this.add(reminderModPanel);
		reminderModPanel.setLayout(null);

		JLabel lblTitolo = new JLabel("Descrizione:");
		lblTitolo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTitolo.setBounds(57, 29, 70, 22);
		reminderModPanel.add(lblTitolo);

		reminderUtilDateModel = new UtilDateModel();
		reminderDatePanel = new JDatePanelImpl(reminderUtilDateModel, new Properties());
		reminderDatePicker = new JDatePickerImpl(reminderDatePanel, new DateLabelFormatter());
		reminderDatePicker.setBounds(134, 64, 390, 23);

		reminderModPanel.add(reminderDatePicker);

		reminderDescriptionTextField = new JTextField();
		reminderDescriptionTextField.setBounds(134, 29, 390, 22);
		reminderModPanel.add(reminderDescriptionTextField);
		reminderDescriptionTextField.setColumns(10);

		JLabel lblData = new JLabel("Data:");
		lblData.setHorizontalAlignment(SwingConstants.RIGHT);
		lblData.setBounds(57, 64, 70, 23);
		reminderModPanel.add(lblData);

		reminderList.addListSelectionListener(new ListSelectionListener() {
		
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (reminderList.isSelectionEmpty()) {
					reminderDescriptionTextField.setText("");
					reminderUtilDateModel.setValue(null);
					deleteReminderButton.setEnabled(false);
					reminderSaveButton.setText("Salva nuovo");
				} else {
					Reminder reminder = reminderList.getSelectedValue();
					reminderDescriptionTextField.setText(reminder.getContent());
					reminderUtilDateModel.setValue(Date.from(
							reminder.getExpirationDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
					deleteReminderButton.setEnabled(true);
					reminderSaveButton.setText("Salva modifiche");
				}

			}
		});

		reminderSaveButton = new JButton("Salva nuovo");
		reminderSaveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (reminderSaveButton.getText().equals("Salva modifiche")) {
					if (!reminderList.isSelectionEmpty()) {
						Reminder reminder = reminderList.getSelectedValue();
						String newDescription = reminderDescriptionTextField.getText();
						LocalDate newDate = LocalDate.parse(reminderDatePicker.getJFormattedTextField().getText());
						try {
							if (!reminder.getContent().equals(newDescription))
								CRMClient.getInstance().updateReminderValue(reminder.getReminderID(), "description",
										newDescription);
							if (!reminder.getContent().equals(newDate))
								CRMClient.getInstance().updateReminderValue(reminder.getReminderID(), "date", newDate);
						} catch ( RemoteException | ServerInternalErrorException
								 | UnathorizedUserException e1) {
							GUIUtils.showCriticalError(e1, parent.getFrame());
						}
						parent.fillEveryForm();
					}

				} else {
					try {						
						if (parent.getContactTable().getSelectedRow() != -1) {
							int customerID = (int) contactTableModel.getValueAt(parent.getContactTable().getSelectedRow(), 0);
							String text = reminderDescriptionTextField.getText();
							LocalDate date = LocalDate.parse(reminderDatePicker.getJFormattedTextField().getText());
							int reminderID = client.saveNewReminder(CustomerType.CONTACT, text, date, customerID);
							Reminder reminder = new Reminder(reminderID, customerID, text, date);
							if (reminderSaveButton.getText().equals("Salva nuovo"))
								reminderListModel.addElement(reminder);
						} else if (parent.getCompanyTable().getSelectedRow() != -1) {
							int customerID = (int) companyTableModel.getValueAt(parent.getCompanyTable().getSelectedRow(), 0);
							String text = reminderDescriptionTextField.getText();
							LocalDate date = LocalDate.parse(reminderDatePicker.getJFormattedTextField().getText());
							int reminderID = client.saveNewReminder(CustomerType.CONTACT, text, date, customerID);
							Reminder reminder = new Reminder(reminderID, customerID, text, date);
							if (reminderSaveButton.getText().equals("Salva nuovo"))
								reminderListModel.addElement(reminder);
						} else {
							JOptionPane.showMessageDialog(parent.getFrame(), "Nessun cliente selezionato!", "Errore!",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (RemoteException | ServerInternalErrorException 
							| UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, parent.getFrame());
						e1.printStackTrace();
					}
				}
			}
		});
		reminderSaveButton.setBounds(402, 100, 122, 25);
		reminderModPanel.add(reminderSaveButton);

		deleteReminderButton = new JButton("Elimina");
		deleteReminderButton.setEnabled(false);
		deleteReminderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.deleteReminder(reminderList.getSelectedValue());
				} catch (RemoteException | ServerInternalErrorException 
						| UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, parent.getFrame());
				}
				parent.fillEveryForm();
			}
		});
		deleteReminderButton.setBounds(12, 13, 97, 25);
		this.add(deleteReminderButton);

		reminderAddButton = new JButton("Aggiungi...");
		reminderAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reminderList.clearSelection();
				reminderDescriptionTextField.setText("");
				reminderDescriptionTextField.grabFocus();
			}
		});
		reminderAddButton.setBounds(319, 13, 97, 25);
		this.add(reminderAddButton);
	}	
	
	public void updateTable(ArrayList<Reminder> list){	
		reminderListModel.clear();
		reminderAddButton.setEnabled(true);
		reminderSaveButton.setEnabled(true);				
		for (Reminder reminder : list) {
			reminderListModel.addElement(reminder);
		}		
	}
}
