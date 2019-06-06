package com.gruppo16.crm.customers;

/**
 * Stato rispetto alla probabilità di creare nuovo business
 */
public enum BusinessState {
	NotAttempted,
	Contacted,
	PossibleNewOpportunity,
	ConfirmedNewOpportunity,
	Disqualified
}