package com.gruppo16.crm.client.gui.employee.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.employee.EmployeeWindow;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.customers.CustomerType;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.files.Note;

public class NotePanel extends JPanel {

	private static final long serialVersionUID = 2306822501591815838L;
	private JList<Note> noteList = new JList<Note>();
	private JTextField noteTitleTextField;
	private JEditorPane noteEditorPane;
	private JButton noteSaveButton;
	private JFrame frame;
	private JTable contactTable;
	private JTable companyTable;
	private TableModel contactTableModel;
	private TableModel companyTableModel;
	private CRMClient client = CRMClient.getInstance();
	private DefaultListModel<Note> noteListModel = new DefaultListModel<Note>();

	public NotePanel(EmployeeWindow parent) {
		super();

		frame = parent.getFrame();
		contactTable = parent.getContactTable();
		companyTable = parent.getCompanyTable();
		contactTableModel = contactTable.getModel();
		companyTableModel = companyTable.getModel();

		this.setLayout(null);

		JScrollPane noteListScrollPane = new JScrollPane();
		noteListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		noteListScrollPane.setBounds(12, 41, 404, 426);
		this.add(noteListScrollPane);

		noteList.setModel(noteListModel);
		noteList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!noteList.isSelectionEmpty()) {
					Note note = noteList.getSelectedValue();
					noteTitleTextField.setText(note.getTitle());
					noteEditorPane.setText(note.getText());
					noteSaveButton.setText("Salva modifiche");
				} else {
					noteTitleTextField.setText("");
					noteEditorPane.setText("");
					noteSaveButton.setText("Salva nuova");
				}
			}
		});

		noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		noteListScrollPane.setViewportView(noteList);

		JPanel noteModPanel = new JPanel();
		noteModPanel.setBounds(414, 0, 591, 467);
		this.add(noteModPanel);
		noteModPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Titolo:");
		lblNewLabel.setBounds(68, 41, 37, 16);
		noteModPanel.add(lblNewLabel);

		noteTitleTextField = new JTextField();
		noteTitleTextField.setBounds(113, 38, 455, 22);
		noteModPanel.add(noteTitleTextField);
		noteTitleTextField.setColumns(10);

		JLabel lblNota = new JLabel("Nota:");
		lblNota.setBounds(68, 70, 37, 16);
		noteModPanel.add(lblNota);

		noteSaveButton = new JButton("Salva nuova");
		noteSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveNote();
			}
		});
		noteSaveButton.setBounds(424, 385, 144, 69);
		noteModPanel.add(noteSaveButton);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(113, 84, 455, 288);
		noteModPanel.add(scrollPane_2);

		noteEditorPane = new JEditorPane();
		scrollPane_2.setViewportView(noteEditorPane);

		JButton deleteNoteButton = new JButton("Elimina");
		deleteNoteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					client.deleteNote(noteList.getSelectedValue());
					parent.fillEveryForm();
					JOptionPane.showMessageDialog(frame, "La nota è stata cancellata!", "Informazione!",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
					GUIUtils.showCriticalError(e1, frame);
				}
			}
		});
		deleteNoteButton.setBounds(12, 13, 97, 25);
		this.add(deleteNoteButton);

		JButton btnCrea = new JButton("Crea...");
		btnCrea.setBounds(317, 13, 97, 25);
		this.add(btnCrea);
		btnCrea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (contactTable.getSelectedRow() == -1 && companyTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(frame, "Selezionare prima un cliente a cui assegnare la nota!",
							"Errore!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				noteList.clearSelection();
				GUIUtils.emptyForms(noteTitleTextField, noteEditorPane);
				noteTitleTextField.grabFocus();
			}
		});
	}

	public void saveNote() {
		if (noteList.isSelectionEmpty()) {
			if (contactTable.getSelectedRow() != -1 || companyTable.getSelectedRow() != -1)
				try {
					int customerID;
					if (contactTable.getSelectedRow() != -1)
						customerID = (int) contactTableModel.getValueAt(contactTable.getSelectedRow(), 0);
					else
						customerID = (int) companyTableModel.getValueAt(companyTable.getSelectedRow(), 0);
					if (!GUIUtils.isEverythingFilled(noteTitleTextField, noteEditorPane))
						JOptionPane.showMessageDialog(frame, "Compilare tutti i campi richiesti!", "Errore!",
								JOptionPane.ERROR_MESSAGE);
					CustomerType type = contactTable.getSelectedRow() != -1 ? CustomerType.CONTACT
							: CustomerType.COMPANY;
					String title = noteTitleTextField.getText();
					String content = noteEditorPane.getText();
					int noteID = client.saveNewNote(type, title, content, customerID);
					Note note = new Note(customerID, noteID, title, content);
					noteListModel.addElement(note);
					JOptionPane.showMessageDialog(frame, "Nota salvata!", "Informazione!",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (UnathorizedUserException | RemoteException | ServerInternalErrorException e) {
					GUIUtils.showCriticalError(e, frame);
					e.printStackTrace();
				}
		} else {
			Note note = noteList.getSelectedValue();
			try {
				client.updateNoteValue(note.getNoteID(), "title", noteTitleTextField.getText());
				client.updateNoteValue(note.getNoteID(), "text", noteEditorPane.getText());
				JOptionPane.showMessageDialog(frame, "Nota salvata!", "Informazione!", JOptionPane.INFORMATION_MESSAGE);
			} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e) {
				GUIUtils.showCriticalError(e, frame);
			}
		}
	}

	public void updateList(ArrayList<Note> list) {
		noteListModel.clear();
		for (Note reminder : list) {
			noteListModel.addElement(reminder);
		}
	}

}
