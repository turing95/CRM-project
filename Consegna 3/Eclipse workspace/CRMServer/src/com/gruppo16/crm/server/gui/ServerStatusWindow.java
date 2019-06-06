package com.gruppo16.crm.server.gui;

//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.util.Date;

public class ServerStatusWindow {

	private JFrame frmStatoDelServer;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private JLabel lblServerStatus;
	
	public void setFrameVisible(){
		frmStatoDelServer.setVisible(true);
	}
	
	public void setServerStatusLabel(boolean online){
		if(online){
			this.lblServerStatus.setForeground(Color.GREEN);
			this.lblServerStatus.setText("SERVER ONLINE");
		}else{
			this.lblServerStatus.setForeground(Color.RED);
			this.lblServerStatus.setText("SERVER OFFLINE");
		}
	}
	
	
	/**
	 * Create the application.
	 */
	public ServerStatusWindow() {
		initialize();
	}
	
	private void updateTextPane(final String text, boolean error) {
	    SwingUtilities.invokeLater(new Runnable() {
	      public void run() {
	        Document doc = textPane.getDocument();
	        SimpleAttributeSet color = new SimpleAttributeSet();
        	if(error)//se devo mostrare un errore scrivo in rosso altrimenti in nero
        		StyleConstants.setForeground(color, Color.RED);
        	else
        		StyleConstants.setForeground(color, Color.BLACK);
        	
	        try {
	        	
	        	if(text.length() > 2){
	        		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        		Date date = new Date();	        		
	        		doc.insertString(doc.getLength(), dateFormat.format(date) + ":  ", null);	
	        	}
	        	
        		doc.insertString(doc.getLength(), text,color);
        		scrollPane.setViewportView(textPane);//metto il textPane modificato nella scrollbar, altrimenti i cambiamenti non sarebbero visibili
	        } catch (BadLocationException e) {
	          throw new RuntimeException(e);
	        }
	        textPane.setCaretPosition(doc.getLength() - 1);
	      }
	    });
	  }

	  private void redirectSystemStreams() {//Faccio in modo che le funzioni System.out e System.err rimandino qui il loro testo e non nella normale console
	    OutputStream out = new OutputStream() {
	      @Override
	      public void write(final int b) throws IOException {
	        updateTextPane(String.valueOf((char) b), false);
	      }

	      @Override
	      public void write(byte[] b, int off, int len) throws IOException {
	        updateTextPane(new String(b, off, len), false);
	      }

	      @Override
	      public void write(byte[] b) throws IOException {
	        write(b, 0, b.length);
	      }	      	     
	    };
	    
	    OutputStream err = new OutputStream() {
		      @Override
		      public void write(final int b) throws IOException {
		        updateTextPane(String.valueOf((char) b), true);
		      }

		      @Override
		      public void write(byte[] b, int off, int len) throws IOException {		    	
		        updateTextPane(new String(b, off, len), true);
		      }

		      @Override
		      public void write(byte[] b) throws IOException {
		        write(b, 0, b.length);
		      }
		    };

	    System.setOut(new PrintStream(out, true));
	    System.setErr(new PrintStream(err, true));
	  }
	
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStatoDelServer = new JFrame();
		frmStatoDelServer.setTitle("Stato del server");
		frmStatoDelServer.setBounds(100, 100, 1273, 753);
		frmStatoDelServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStatoDelServer.setResizable(false);		
		frmStatoDelServer.getContentPane().setLayout(null);
		frmStatoDelServer.setLocationRelativeTo(null);
		
		redirectSystemStreams();
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(124, 136, 1031, 506);
		frmStatoDelServer.getContentPane().add(scrollPane);
		textPane = new JTextPane();
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		
		lblServerStatus = new JLabel("SERVER OFFLINE");
		lblServerStatus.setFont(new Font("SansSerif", Font.BOLD, 40));
		lblServerStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerStatus.setForeground(Color.RED);
		lblServerStatus.setBounds(124, 71, 1031, 52);
		frmStatoDelServer.getContentPane().add(lblServerStatus);
		
		JButton btnNewButton = new JButton("Chiudi server");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnNewButton.setBounds(524, 655, 184, 50);
		frmStatoDelServer.getContentPane().add(btnNewButton);
		
	}
}
