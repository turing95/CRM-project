package com.gruppo16.crm.client.gui.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.gruppo16.crm.client.core.rmi.CRMClient;
import com.gruppo16.crm.client.gui.utils.GUIUtils;
import com.gruppo16.crm.exceptions.BadLoginException;
import com.gruppo16.crm.exceptions.ServerInternalErrorException;



public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -4636358821402508872L;
	private static LoginDialog dialog;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField;
	private JTextField usernameField;
	private static CRMClient client = CRMClient.getInstance();
	
	public static void apri() {
		dialog = new LoginDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Create the dialog.
	 */
	public LoginDialog() {
		GUIUtils.setLookAndFeel();
		setFont(new Font("Dialog", Font.PLAIN, 25));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);		
		setTitle("                                                                         Login");	
		setBounds(100, 100, 720, 293);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);//Sposta la finestra al centro dello schermo
		contentPanel.setLayout(null);
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(58, 13, 119, 48);
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 25));
		contentPanel.add(lblUsername);
		
		JLabel lblDatiErrati = new JLabel("Dati errati!");
		lblDatiErrati.setBounds(189, 136, 438, 31);
		lblDatiErrati.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblDatiErrati.setVisible(false);
		lblDatiErrati.setForeground(Color.RED);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(189, 75, 438, 48);
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 25));
		contentPanel.add(passwordField);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(12, 74, 165, 44);
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 25));
		contentPanel.add(lblPassword);
				
		usernameField = new JTextField();		
		usernameField.setBounds(189, 14, 438, 48);
		usernameField.setFont(new Font("Tahoma", Font.PLAIN, 25));
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(Character.isLetterOrDigit(e.getKeyChar())){
					lblDatiErrati.setVisible(false);
				}				
			}
		});
		contentPanel.add(usernameField);
		usernameField.setColumns(10);
		

		contentPanel.add(lblDatiErrati);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 180, 714, 78);
			contentPanel.add(buttonPane);
			{
				JButton okButton = new JButton("Login");
				okButton.setBounds(476, 13, 226, 47);
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {												

						String username = usernameField.getText();							
						char pass[] = passwordField.getPassword();

						try{		
							client.login(username, pass);
							lblDatiErrati.setVisible(false);							
							dispose();//Chiude la finestra di login
						} catch (BadLoginException | ServerInternalErrorException e1) {
							lblDatiErrati.setText(e1.getMessage());						
							lblDatiErrati.setVisible(true);			
						}catch (RemoteException e1){
							GUIUtils.showCriticalError(e1, dialog);
						}
					}
				});
				buttonPane.setLayout(null);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Annulla");
				cancelButton.setBounds(243, 13, 221, 47);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
