package com.gruppo16.crm.server.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.gruppo16.crm.server.gui.ServerStatusWindow;

public class ServerMain {

	private static ServerStatusWindow window;

	public static void main(String[] args) {

		try {
			window = new ServerStatusWindow();
			window.setFrameVisible();
		} catch (Exception e) {
			System.err.println("Errore nell'apertura della gui:");
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			System.out.println("Avviamento del server...");
			Registry registry = LocateRegistry.createRegistry(12345);
			registry.rebind("CRMServer", new CRMServer());
			window.setServerStatusLabel(true);
			System.out.println("Server online");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
