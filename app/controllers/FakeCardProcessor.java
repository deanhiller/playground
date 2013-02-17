package controllers;

import java.util.HashMap;
import java.util.Map;

import com.playground.qatests.CreditCard;
import com.playground.qatests.CreditCardProcessor;
import com.playground.qatests.RecurringInfo;
import com.playground.qatests.Result;

public class FakeCardProcessor implements CreditCardProcessor {

	@Override
	public Result charge(CreditCard card, String amountStr) {
		HashMap status = new HashMap();
		status.put("dc_transaction_id", "1234567777");
		status.put("dc_response_code", "00");
		return new Result(status);
	}

	@Override
	public Result scheduleRecurringCharge(String transactionId,
			RecurringInfo info) {
		HashMap status = new HashMap();
		status.put("dc_transaction_id", "1234567777");
		status.put("dc_response_code", "00");
		return new Result(status);
	}
}
