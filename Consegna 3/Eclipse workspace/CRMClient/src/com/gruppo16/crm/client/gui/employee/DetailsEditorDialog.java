package com.gruppo16.crm.client.gui.employee;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.employees.Employee;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;
import com.gruppo16.crm.exceptions.UnathorizedUserException;
import com.gruppo16.crm.exceptions.WrongOldPasswordException;

public class DetailsEditorDialog extends JDialog {

	private static final long serialVersionUID = 1545111054536789349L;
	private final JPanel contentPanel = new JPanel();
	private JTextField firstNameTextField;
	private JTextField surnameTextField;
	private JTextField emailTextField;
	private JTextField phoneNumberTextField;
	private JPasswordField newPasswordField;
	private JPasswordField oldPasswordField;
	private JPasswordField confirmPasswordField;
	private CRMClient client;
	private Employee loggedEmployee;
	private static JDialog dialog;

	/**
	 * Launch the application.
	 */
	public static void showMe() {
		try {
			dialog = new DetailsEditorDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DetailsEditorDialog() {
		client = CRMClient.getInstance();
		loggedEmployee = client.getLoggedEmployee();
		setBounds(100, 100, 688, 398);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblModificaDeiPropri = new JLabel("Modifica dei propri dati personali");
			lblModificaDeiPropri.setBounds(12, 12, 187, 16);
			contentPanel.add(lblModificaDeiPropri);
		}
		{
			JLabel lblNome = new JLabel("Nome:");
			lblNome.setBounds(161, 39, 38, 16);
			contentPanel.add(lblNome);
		}
		{
			firstNameTextField = new JTextField();
			firstNameTextField.setBounds(206, 36, 211, 22);
			firstNameTextField.setText(loggedEmployee.getFirstName());
			contentPanel.add(firstNameTextField);
			firstNameTextField.setColumns(10);
		}
		{
			JButton btnCambia = new JButton("Cambia nome");
			btnCambia.setBounds(424, 35, 241, 25);
			btnCambia.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						client.updateEmployeeValue(loggedEmployee.getEmployeeID(), "firstName",
								firstNameTextField.getText());
						JOptionPane.showMessageDialog(dialog, "Nome cambiato con successo!", "",
								JOptionPane.INFORMATION_MESSAGE);
						loggedEmployee.setFirstName(firstNameTextField.getText());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, dialog);
					}
				}
			});
			contentPanel.add(btnCambia);
		}
		{
			JLabel lblCognome = new JLabel("Cognome:");
			lblCognome.setBounds(140, 71, 59, 16);
			contentPanel.add(lblCognome);
		}
		{
			surnameTextField = new JTextField();
			surnameTextField.setBounds(206, 68, 211, 22);
			surnameTextField.setText(loggedEmployee.getSurname());
			contentPanel.add(surnameTextField);
			surnameTextField.setColumns(10);
		}
		{
			JButton btnCambia_1 = new JButton("Cambia cognome");
			btnCambia_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						client.updateEmployeeValue(loggedEmployee.getEmployeeID(), "surname",
								surnameTextField.getText());
						JOptionPane.showMessageDialog(dialog, "Cognome cambiato con successo!", "",
								JOptionPane.INFORMATION_MESSAGE);
						loggedEmployee.setSurname(surnameTextField.getText());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, dialog);
					}
				}
			});
			btnCambia_1.setBounds(424, 67, 241, 25);
			contentPanel.add(btnCambia_1);
		}
		{
			JLabel lblEmail = new JLabel("Email:");
			lblEmail.setBounds(163, 103, 36, 16);
			contentPanel.add(lblEmail);
		}
		{
			emailTextField = new JTextField();
			emailTextField.setBounds(206, 100, 211, 22);
			emailTextField.setText(loggedEmployee.getEmail());
			contentPanel.add(emailTextField);
			emailTextField.setColumns(10);
		}
		{
			JButton btnCambia_2 = new JButton("Cambia email");
			btnCambia_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						client.updateEmployeeValue(loggedEmployee.getEmployeeID(), "email", emailTextField.getText());
						JOptionPane.showMessageDialog(dialog, "Email cambiata con successo!", "",
								JOptionPane.INFORMATION_MESSAGE);
						loggedEmployee.setEmail(emailTextField.getText());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, dialog);
					}
				}
			});
			btnCambia_2.setBounds(424, 99, 241, 25);
			contentPanel.add(btnCambia_2);
		}
		{
			JLabel lblTelefono = new JLabel("Telefono:");
			lblTelefono.setBounds(144, 135, 55, 16);
			contentPanel.add(lblTelefono);
		}
		{
			phoneNumberTextField = new JTextField();
			phoneNumberTextField.setBounds(206, 132, 211, 22);
			phoneNumberTextField.setText(loggedEmployee.getPhoneNumber());
			contentPanel.add(phoneNumberTextField);
			phoneNumberTextField.setColumns(10);
		}
		{
			JButton btnCambia_3 = new JButton("Cambia telefono");
			btnCambia_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						client.updateEmployeeValue(loggedEmployee.getEmployeeID(), "phoneNumber",
								phoneNumberTextField.getText());
						JOptionPane.showMessageDialog(dialog, "Numero di telefono cambiato con successo!", "",
								JOptionPane.INFORMATION_MESSAGE);
						loggedEmployee.setPhoneNumber(phoneNumberTextField.getText());
					} catch (RemoteException | ServerInternalErrorException | UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, dialog);
					}
				}
			});
			btnCambia_3.setBounds(424, 131, 241, 25);
			contentPanel.add(btnCambia_3);
		}
		{
			JLabel lblPassword = new JLabel("Vecchia password:");
			lblPassword.setBounds(91, 197, 108, 16);
			contentPanel.add(lblPassword);
		}
		{
			oldPasswordField = new JPasswordField();
			oldPasswordField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					oldPasswordField.setText("");
				}
			});
			oldPasswordField.setText("***********");
			oldPasswordField.setBounds(206, 194, 211, 22);
			contentPanel.add(oldPasswordField);
		}
		{
			JLabel lblConfermaPassword = new JLabel("Nuova password:");
			lblConfermaPassword.setBounds(100, 226, 99, 16);
			contentPanel.add(lblConfermaPassword);
		}
		{
			newPasswordField = new JPasswordField();
			newPasswordField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					newPasswordField.setText("");
				}
			});
			newPasswordField.setBounds(206, 223, 211, 22);
			newPasswordField.setText("***********");
			contentPanel.add(newPasswordField);
		}
		{
			JLabel lblConfermaPassword_1 = new JLabel("Conferma password:");
			lblConfermaPassword_1.setBounds(79, 256, 120, 16);
			contentPanel.add(lblConfermaPassword_1);
		}
		{
			confirmPasswordField = new JPasswordField();
			confirmPasswordField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					confirmPasswordField.setText("");
				}
			});
			confirmPasswordField.setBounds(206, 253, 211, 22);
			confirmPasswordField.setText("***********");
			contentPanel.add(confirmPasswordField);
		}
		{
			JButton btnCambiaPassword = new JButton("Cambia password...");
			btnCambiaPassword.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (!Arrays.equals(newPasswordField.getPassword(), confirmPasswordField.getPassword())) {
							JOptionPane.showMessageDialog(dialog, "Le password non corrispondono!", "Errore!",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						client.changePassword(oldPasswordField.getPassword(), newPasswordField.getPassword());
						JOptionPane.showMessageDialog(dialog, "Password cambiata con successo", "",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (RemoteException | WrongOldPasswordException | ServerInternalErrorException
							| UnathorizedUserException e1) {
						GUIUtils.showCriticalError(e1, dialog);
					}
				}
			});
			btnCambiaPassword.setBounds(424, 252, 241, 25);
			contentPanel.add(btnCambiaPassword);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Esci");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("OK");
				buttonPane.add(cancelButton);
			}
		}
	}

}
