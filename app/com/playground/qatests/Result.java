package com.playground.qatests;

import java.util.HashMap;

public class Result {

	private String transactionId;
	private String responseMsg;
	private String responseCode;
	private HashMap map;

	public Result(HashMap result) {
		transactionId = (String)result.get ("dc_transaction_id");
		responseMsg = (String) result.get("dc_response_message");
		responseCode = (String)result.get("dc_response_code");
		this.map = result;
	}

	public String getTransactionId() {
		return transactionId;
	}
	
	public boolean isFailed() {
		if("00".equals(responseCode) || "85".equals(responseCode))
			return false;
		return true;
	}
	
	public String getErrorMessage() {
		return responseMsg;
	}

	public HashMap getMap() {
		return map;
	}
	
}
