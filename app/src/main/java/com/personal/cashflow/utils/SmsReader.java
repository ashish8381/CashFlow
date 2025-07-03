package com.personal.cashflow.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.personal.cashflow.room.TransactionModel;
import com.personal.cashflow.room.TransactionRepository;

public class SmsReader {

    public interface OnScanCompleteListener {
        void onScanComplete(int count);
    }

    public static void scanSmsInBackground(Context context, OnScanCompleteListener listener) {
        new Thread(() -> {
            int addedCount = 0;
            long lastTimestamp = PrefUtils.getLastSmsTimestamp(context);

            Uri uriSms = Uri.parse("content://sms/inbox");
            ContentResolver cr = context.getContentResolver();

            String selection = "date > ?";
            String[] selectionArgs = new String[]{String.valueOf(lastTimestamp)};
            String sortOrder = "date ASC";

            try (Cursor cursor = cr.query(uriSms, null, selection, selectionArgs, sortOrder)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int bodyIndex = cursor.getColumnIndex("body");
                    int dateIndex = cursor.getColumnIndex("date");

                    long maxTimestamp = lastTimestamp;

                    while (!cursor.isAfterLast()) {
                        String body = cursor.getString(bodyIndex);
                        long timestamp = cursor.getLong(dateIndex);

                        if (body.contains("debited") || body.contains("credited")) {
                            TransactionModel txn = SmsParser.parseMessage(body);
                            if (txn != null) {
                                TransactionRepository.getInstance(context).insertTransaction(txn);
                                Log.d("DB_INSERT", "Inserted txn: " + txn.smsContent);

                                addedCount++;
                                if (timestamp > maxTimestamp)
                                    maxTimestamp = timestamp;
                            }
                        }

                        cursor.moveToNext();
                    }

                    // Save the latest timestamp
                    PrefUtils.setLastSmsTimestamp(context, maxTimestamp);
                }
            }

            if (listener != null) {
                int finalCount = addedCount;
                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onScanComplete(finalCount);
                });
            }
        }).start();
    }
}


