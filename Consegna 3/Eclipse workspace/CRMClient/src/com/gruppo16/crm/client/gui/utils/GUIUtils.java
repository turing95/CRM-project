package com.gruppo16.crm.client.gui.utils;

import java.awt.Component;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

/**
 * Contiene vari metodi {@code static} utili per riassumere operazioni ripetute
 * più volte durante l'utilizzo della GUI, come ad esempio pulizia di form e
 * finestre di errore
 * 
 * @author gruppo16
 *
 */
public class GUIUtils {

	/**
	 * Chiamare in caso di errore grave. Mostra una finestra di errore con il
	 * {@code message} dell'{@code Exception}. Se si tratta di
	 * un'{@code Exception} che impedirebbe il normale funzionamento del client
	 * esso viene chiuso
	 * 
	 * @param e
	 *            L'exception lanciata
	 * @param frame
	 *            La finestra su cui mostrare la finestra di errore
	 */
	public static void showCriticalError(Exception e, Component window) {
		e.printStackTrace();// TODO per ora lo lascio per il debug, dopo si può
							// togliere
		String message = "Si è verificato un errore critico!\n" + e.getMessage()
				+ "\nSe l'errore persise contattare un amministratore di sistema.";		
		boolean toClose = false;
		if (e instanceof RemoteException){			
			toClose = true;
			message += "\nIl programma sarà chiuso!";
		}
		JOptionPane.showMessageDialog(window, message, "Errore critico!", JOptionPane.ERROR_MESSAGE);
		if(toClose)
			System.exit(-1);
	}

	/** Svuota tutte le caselle di testo passate come parametro */
	public static void emptyForms(JTextComponent... forms) {
		for (JTextComponent form : forms)
			form.setText("");
	}

	/**
	 * Controlla se le caselle di testo passate per parametro contengano tutte
	 * almeno un carattere
	 * 
	 * @param forms
	 * @return {@code true} se tutte le caselle di testo contengono almeno un
	 *         carattere, {@code false} altrimenti
	 */
	public static boolean isEverythingFilled(JTextComponent... forms) {
		for (JTextComponent form : forms) {
			if (form.getText().isEmpty())
				return false;
		}
		return true;
	}

	/**
	 * Rimuove tutti gli elementi dalle {@code JTable} i cui modelli sono
	 * passati per parametro
	 */
	public static void resetTables(DefaultTableModel... tableModels) {
		for (DefaultTableModel tableModel : tableModels) {
			int max = tableModel.getRowCount();
			for (int i = max - 1; i >= 0; i--)
				tableModel.removeRow(i);
		}
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
