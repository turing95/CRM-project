package com.gruppo16.crm.client.core;

import java.util.ArrayList;
import java.util.Arrays;

import com.gruppo16.crm.Update;

public class UpdateHandler {

	ArrayList<Update> updates = new ArrayList<Update>();

	public UpdateHandler() {
	}

	public UpdateHandler(Update... updates) {
		this.updates = new ArrayList<Update>(Arrays.asList(updates));
	}

	public void addUpdate(Update update) {
		updates.add(update);
	}
	
	public void performUpdates(){
		
	}

}
