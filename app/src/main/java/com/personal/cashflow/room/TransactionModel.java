package com.personal.cashflow.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "transactions")
public class TransactionModel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String type; // debited / credited
    public String merchant;
    public String date;
//    public String time;
    public String smsContent;

    public String mode;

    @ColumnInfo(name = "source")
    public String source;

    @ColumnInfo(name = "ref_no")
    public String refNo;


    public String getMonthYear() {
        try {
            // Convert "29Jun25" to "June 2025"
            SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            Date parsedDate = inputFormat.parse(date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            return "Unknown";
        }
    }

}

