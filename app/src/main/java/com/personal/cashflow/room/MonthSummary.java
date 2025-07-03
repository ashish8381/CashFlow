package com.personal.cashflow.room;

import java.util.ArrayList;
import java.util.List;

public class MonthSummary {
    public String monthYear;
    public List<TransactionModel> transactions;
    public double totalDebit;
    public double totalCredit;

    public MonthSummary(String monthYear) {
        this.monthYear = monthYear;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(TransactionModel txn) {
        transactions.add(txn);
        if (txn.type.equalsIgnoreCase("debited")) {
            totalDebit += txn.amount;
        } else if (txn.type.equalsIgnoreCase("credited")) {
            totalCredit += txn.amount;
        }
    }
}

