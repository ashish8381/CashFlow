package com.personal.cashflow.utils;

import android.util.Log;

import com.personal.cashflow.room.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsParser {

    public static TransactionModel parseMessage(String message) {
        TransactionModel txn;

        // 1. Most specific and exact matchers FIRST
        txn = parseBoiCreditMessage(message);
        if (txn != null) return txn;

        txn = parseFederalCreditMessage(message);
        if (txn != null) return txn;

        txn = parseBoiStyleDebitMessage(message);
        if (txn != null) return txn;

        txn = parseFederalStyleDebitMessage(message);
        if (txn != null) return txn;

        // 2. More generic / fallback last
        txn = parseGenericUpiBankStyle(message);
        if (txn != null) return txn;

        // Optional: log unhandled messages
        Log.w("SMS_UNPARSER", "Unmatched SMS: " + message);



//        txn = parseGenericUpiBankStyle(message); // Covers HDFC, ICICI, SBI, Axis
//        if (txn != null) return txn;

        return null;
    }

    // BOI / Short format
    /* ------------------------------------------------------------------
     * 1. BOI   •  Debit (simple UPI transfer with “debited … to …”)
     * ------------------------------------------------------------------ */
    private static TransactionModel parseBoiStyleDebitMessage(String msg) {
        Log.d("SMS_PARSER", "Attempting BOI Debit parse: " + msg);

        Pattern p = Pattern.compile(
                "Rs\\.?\\s?(\\d+(?:\\.\\d+)?)\\s+debited\\b(?!.*\\bcredited\\b).*?to\\s+(.*?)\\s+(?:via|by).*?on\\s+(\\d{1,2}[A-Za-z]{3}\\d{2})",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        if (!m.find()) return null;

        TransactionModel t = new TransactionModel();
        t.amount   = Double.parseDouble(m.group(1));
        t.type     = "debited";
        t.merchant = m.group(2).trim();
        t.date     = m.group(3);
        t.mode     = "UPI";
        t.source   = extractBankFromSuffix(msg);   // “BOI”
        t.smsContent = msg;
        return t;
    }

    /* ------------------------------------------------------------------
     * 2. FEDERAL • Debit (UPI – long date & time)
     * ------------------------------------------------------------------ */
    private static TransactionModel parseFederalStyleDebitMessage(String msg) {

        Log.d("SMS_PARSER", "Attempting FII Debit parse: " + msg);
        Pattern p = Pattern.compile(
                "Rs\\s?(\\d+(?:\\.\\d+)?)\\s+debited\\b(?!.*\\bcredited\\b).*?on\\s+(\\d{2}-\\d{2}-\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2}).*?to\\s+VPA\\s+(\\S+)",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        if (!m.find()) return null;

        TransactionModel t = new TransactionModel();
        t.amount   = Double.parseDouble(m.group(1));
        t.type     = "debited";
        t.date     = formatDate(m.group(2));          // dd-MM-yyyy ➜ ddMMMyy
        t.merchant = m.group(4);
        t.mode     = "UPI";
        t.source   = extractBankFromSuffix(msg);      // “Federal Bank”
        t.smsContent = msg;
        return t;
    }

    /* ------------------------------------------------------------------
     * 3. FEDERAL • Credit (IMPS)
     * ------------------------------------------------------------------ */
    private static TransactionModel parseFederalCreditMessage(String msg) {
        Log.d("SMS_PARSER", "Attempting FII credit parse: " + msg);

        Pattern p = Pattern.compile(
                "Rs\\s?(\\d+(?:\\.\\d+)?)\\s+credited.*?A/c\\s+(\\S+).*?on\\s+(\\d{2}[A-Z]{3}\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2}).*?Ref\\s+no\\s+(\\d+).*?Bal:Rs\\s?([0-9,.]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        if (!m.find()) return null;

        TransactionModel t = new TransactionModel();
        t.amount   = Double.parseDouble(m.group(1));
        t.type     = "credited";
        t.date     = formatTextualDate(m.group(3));   // 12MAY2025 ➜ 12May25
        t.refNo    = m.group(5);
        t.mode     = "IMPS";
        t.source   = extractBankFromSuffix(msg);      // “Federal Bank”
        t.smsContent = msg;
        return t;
    }

    /* ------------------------------------------------------------------
     * 4. BOI   •  Credit (UPI)
     * ------------------------------------------------------------------ */
    private static TransactionModel parseBoiCreditMessage(String msg) {
        Log.d("SMS_PARSER", "Attempting BOI credit parse: " + msg);
        Pattern p = Pattern.compile(
                "Rs\\.?\\s?(\\d+(?:\\.\\d+)?)\\s+Credited to your Ac\\s+(\\S+)\\s+on\\s+(\\d{2}-\\d{2}-\\d{2})\\s+by\\s+UPI\\s+ref\\s+No\\.?\\s*(\\d+).*?Avl Bal\\s+([0-9,.]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(msg);
        if (!m.find()) return null;

        TransactionModel t = new TransactionModel();
        t.amount   = Double.parseDouble(m.group(1));
        t.type     = "credited";
        t.date     = formatShortDate(m.group(3));     // 11‑06‑25 ➜ 11Jun25
        t.refNo    = m.group(4);
        t.mode     = "UPI";
        t.source   = extractBankFromSuffix(msg);      // “BOI”
        t.smsContent = msg;
        return t;
    }



    // Generic parser for HDFC, ICICI, Axis, SBI etc.
    private static TransactionModel parseGenericUpiBankStyle(String message) {
        Pattern pattern = Pattern.compile(
                "Rs\\.?\\s?(\\d+(?:\\.\\d+)?)\\s+(debited|credited).*?to\\s+(.*?)(?:\\.|via|on).*?(\\d{2}-\\d{2}-\\d{4}|\\d{1,2}[A-Za-z]{3}\\d{2})"
        );
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            TransactionModel txn = new TransactionModel();
            txn.amount = Double.parseDouble(matcher.group(1));
            txn.type = matcher.group(2).toLowerCase();
            txn.merchant = matcher.group(3).trim();
            txn.date = formatAnyDate(matcher.group(4));
            txn.source = extractBankFromSuffix(message);
            txn.smsContent = message;
            return txn;
        }
        return null;
    }

    // Extracts "Axis Bank", "HDFC Bank", "Federal Bank" etc.
    private static String extractBankFromSuffix(String message) {
        Matcher bankMatcher = Pattern.compile("-(\\w[\\w\\s.&-]+)[\\s.!]*$").matcher(message);
        if (bankMatcher.find()) {
            return bankMatcher.group(1).trim();
        }
        return "Unknown";
    }

    private static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date); // e.g., 27Jun25
        } catch (ParseException e) {
            return "Unknown";
        }
    }

    private static String formatAnyDate(String inputDate) {
        try {
            if (inputDate.contains("-")) {
                return formatDate(inputDate); // dd-MM-yyyy
            } else {
                return inputDate; // already in ddMMMyy
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String formatTextualDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date); // e.g., 12May25
        } catch (ParseException e) {
            return "Unknown";
        }
    }

    private static String formatShortDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date); // e.g., 11Jun25
        } catch (ParseException e) {
            return "Unknown";
        }
    }


}
