package com.gruppo16.crm.client.gui.employee.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

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
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.gruppo16.crm.client.core.factory.EventFactory;
import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.exceptions.EmptyFormsException;
import com.gruppo16.crm.client.gui.utils.DateLabelFormatter;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.events.ConferenceCall;
import com.gruppo16.crm.events.Event;
import com.gruppo16.crm.events.Meeting;
import com.gruppo16.crm.events.Order;
import com.gruppo16.crm.events.PhoneCall;
import com.gruppo16.crm.exceptions.InexistingColumnException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;

public class EventPanel extends JPanel{

	private static final long serialVersionUID = -8934041189380930223L;

	private JList<Event> eventList = new JList<Event>();
	private DefaultListModel<Event> eventListModel = new DefaultListModel<Event>();
	private JTextField eventDescriptionTextField;
	private JComboBox<?> eventComboBox;
	private JButton eventSaveButton;
	private JDatePickerImpl eventDatePicker;
	private DateEditor de_EventTimeSpinner;
	private CRMClient client = CRMClient.getInstance();
	private JFrame frame;
	private JTable contactTable;
	private JTable companyTable;
	private TableModel contactTableModel;
	private TableModel companyTableModel;
	private EmployeeWindow parent;

	public EventPanel(EmployeeWindow parent) {
		super();
		this.parent = parent;
		this.setLayout(null);

		frame = parent.getFrame();
		contactTable = parent.getContactTable();
		companyTable = parent.getCompanyTable();
		contactTableModel = contactTable.getModel();
		companyTableModel = companyTable.getModel();

		JScrollPane conferenceCallScrollPane = new JScrollPane();
		conferenceCallScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		conferenceCallScrollPane.setBounds(12, 39, 396, 415);
		this.add(conferenceCallScrollPane);

		eventList.setModel(eventListModel);
		eventList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!eventList.isSelectionEmpty()) {
					Event event = eventList.getSelectedValue();
					eventDescriptionTextField.setText(event.getDescription());
					if (event instanceof PhoneCall)
						eventComboBox.setSelectedItem("Telefonata");
					else if (event instanceof Meeting)
						eventComboBox.setSelectedItem("Meeting");
					else if (event instanceof ConferenceCall)
						eventComboBox.setSelectedItem("Conference call");
					eventSaveButton.setText("Salva modifiche");
				} else {
					eventDescriptionTextField.setText("");
					eventComboBox.setSelectedIndex(-1);
					eventSaveButton.setText("Salva nuovo");
				}

			}
		});
		conferenceCallScrollPane.setViewportView(eventList);

		JPanel eventModPanel = new JPanel();
		eventModPanel.setBounds(422, 0, 595, 454);
		this.add(eventModPanel);
		eventModPanel.setLayout(null);

		UtilDateModel conferenceCallUtilDateModel = new UtilDateModel();
		JDatePanelImpl conferenceCallDatePanel = new JDatePanelImpl(conferenceCallUtilDateModel, new Properties());
		eventDatePicker = new JDatePickerImpl(conferenceCallDatePanel, new DateLabelFormatter());
		eventDatePicker.setBounds(103, 75, 420, 22);

		eventModPanel.add(eventDatePicker);

		JSpinner EventTimeSpinner = new JSpinner(new SpinnerDateModel());
		de_EventTimeSpinner = new JSpinner.DateEditor(EventTimeSpinner, "HH:mm");
		EventTimeSpinner.setEditor(de_EventTimeSpinner);
		EventTimeSpinner.setValue(new Date()); // will only show the
												// current time
		EventTimeSpinner.setBounds(525, 75, 56, 22);

		eventModPanel.add(EventTimeSpinner);

		JLabel lblDescrizione = new JLabel("Descrizione:");
		lblDescrizione.setBounds(22, 41, 79, 16);
		eventModPanel.add(lblDescrizione);

		eventDescriptionTextField = new JTextField();
		eventDescriptionTextField.setBounds(103, 38, 478, 22);
		eventModPanel.add(eventDescriptionTextField);
		eventDescriptionTextField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Data e ora:");
		lblNewLabel_1.setBounds(22, 81, 69, 16);
		eventModPanel.add(lblNewLabel_1);

		eventSaveButton = new JButton("Salva nuovo");
		eventSaveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				saveEvent();
			}
		});
		eventSaveButton.setBounds(435, 416, 148, 25);
		eventModPanel.add(eventSaveButton);

		JButton btnCreaNuovo = new JButton("Crea nuovo");
		btnCreaNuovo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (contactTable.getSelectedRow() == -1 && companyTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(frame, "Selezionare prima un cliente a cui assegnare l'evento!",
							"Errore!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				eventList.clearSelection();
				GUIUtils.emptyForms(eventDescriptionTextField);
				eventDescriptionTextField.grabFocus();
			}
		});
		btnCreaNuovo.setBounds(22, 416, 97, 25);
		eventModPanel.add(btnCreaNuovo);

		eventComboBox = new JComboBox();
		eventComboBox.setModel(new DefaultComboBoxModel(
				new String[] { "<Selezionare un evento>", "Conference call", "Telefonata", "Meeting" }));
		eventComboBox.setBounds(103, 110, 478, 22);
		eventModPanel.add(eventComboBox);

		JLabel lblTipoDiEvento = new JLabel("Tipo di evento:");
		lblTipoDiEvento.setBounds(12, 113, 89, 16);
		eventModPanel.add(lblTipoDiEvento);

		JButton btnNewButton_3 = new JButton("Elimina");
		btnNewButton_3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.removeEvent(eventList.getSelectedValue());
					parent.fillEveryForm();
				} catch (RemoteException | ServerInternalErrorException 
						| UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, frame);
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_3.setBounds(12, 13, 97, 25);
		this.add(btnNewButton_3);
	}

	public void saveEvent() {
		if (eventList.isSelectionEmpty()) {
			if (contactTable.getSelectedRow() != -1 || companyTable.getSelectedRow() != -1)
				try {
					int customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
					if (!GUIUtils.isEverythingFilled(eventDescriptionTextField)
							&& eventComboBox.getSelectedIndex() != -1)
						throw new EmptyFormsException();
					EventFactory f = new EventFactory();
					CustomerType type = contactTable.getSelectedRow() != -1 ? CustomerType.CONTACT
							: CustomerType.COMPANY;
					LocalDateTime date = LocalDateTime.parse(eventDatePicker.getJFormattedTextField().getText() + "T"
							+ de_EventTimeSpinner.getTextField().getText());
					Event event = f.createEvent(type, (String) eventComboBox.getSelectedItem(), customerID,
							eventDescriptionTextField.getText(), date);
					eventListModel.addElement(event);
					JOptionPane.showMessageDialog(frame, "Evento salvato!", "Informazione!",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (EmptyFormsException | RemoteException | ServerInternalErrorException e1) {
					JOptionPane.showMessageDialog(frame, "Impossibile salvare l'evento!\n" + e1.getMessage(), "Errore!",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(frame, "Tipo di evento non selezionato!", "Errore!",
							JOptionPane.ERROR_MESSAGE);
				}
		} else {
			System.out.println("dksjasdl");
			Event event = eventList.getSelectedValue();
			try {
				client.updateEventValue(event, "description", eventDescriptionTextField.getText());
				JOptionPane.showMessageDialog(frame, "Evento salvato!", "Informazione!",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (RemoteException | ServerInternalErrorException | InexistingColumnException
					 | UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, parent.getFrame());
				e.printStackTrace();
			}

		}
	}

	public void updateList(ArrayList<Event> list) {
		eventListModel = new DefaultListModel<>();
		for (Event event : list) {
			if (!(event instanceof Order))
				eventListModel.addElement(event);
		}
		eventList.setModel(eventListModel);
	}

}
