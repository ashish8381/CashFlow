package com.personal.cashflow.utils;

import com.personal.cashflow.room.MonthSummary;
import com.personal.cashflow.room.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MonthlyGrouper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

    public static List<MonthSummary> groupByMonth(List<TransactionModel> allTxns) {
        Map<String, MonthSummary> map = new HashMap<>();
        for (TransactionModel txn : allTxns) {
            String key = txn.getMonthYear(); // e.g., "May 2025"
            if (!map.containsKey(key)) {
                map.put(key, new MonthSummary(key));
            }
            map.get(key).addTransaction(txn);
        }

        // Sort the keys by actual month-year date
        List<MonthSummary> sortedList = new ArrayList<>(map.values());
        sortedList.sort((m1, m2) -> {
            try {
                Date d1 = sdf.parse(m1.monthYear);
                Date d2 = sdf.parse(m2.monthYear);
                return d2.compareTo(d1); // Newest month first
            } catch (ParseException e) {
                return 0;
            }
        });

        return sortedList;
    }
}
