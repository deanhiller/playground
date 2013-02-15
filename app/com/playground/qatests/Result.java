package com.playground.qatests;

import java.util.HashMap;

public class Result {

	private String transactionId;

	public Result(HashMap result) {
		transactionId = (String)result.get ("dc_transaction_id");
	}

	public String getTransactionId() {
		return transactionId;
	}
	
	public boolean isFailed() {
		return true;
	}
	
	public String getErrorMessage() {
		return "Your credit card transaction failed";
	}
}
