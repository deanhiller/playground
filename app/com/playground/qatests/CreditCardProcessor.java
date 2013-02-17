package com.playground.qatests;

import java.util.HashMap;

public interface CreditCardProcessor {

	Result charge(CreditCard card, String amountStr);

	Result scheduleRecurringCharge(String transactionId, RecurringInfo info);

}
