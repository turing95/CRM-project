package com.gruppo16.crm.client.gui.login;
import java.awt.Window.Type;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ConnectingToServerFrame {

	private JFrame frame;
	private static ConnectingToServerFrame window;

	/**
	 * Launch the application.
	 */
	public static void chiudi(){
		window.frame.dispose();
	}
	public static void apri() {
		try {
			ConnectingToServerFrame.window = new ConnectingToServerFrame();
			window.frame.setVisible(true);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public ConnectingToServerFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(){
		frame = new JFrame();
		frame.setType(Type.UTILITY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 515, 147);	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);	
		frame.setLocationRelativeTo(null);
		
		JLabel lblConnessioneAlServer = new JLabel("Connessione al server...");
		lblConnessioneAlServer.setBounds(173, 35, 162, 32);
		frame.getContentPane().add(lblConnessioneAlServer);		
	}

}
