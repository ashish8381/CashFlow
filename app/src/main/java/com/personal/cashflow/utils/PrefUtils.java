package com.personal.cashflow.utils;

import android.content.Context;

public class PrefUtils {
    private static final String PREF_NAME = "transaction_prefs";
    private static final String KEY_LAST_SMS_TIMESTAMP = "last_sms_timestamp";

    public static long getLastSmsTimestamp(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_SMS_TIMESTAMP, 0);
    }

    public static void setLastSmsTimestamp(Context context, long timestamp) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putLong(KEY_LAST_SMS_TIMESTAMP, timestamp).apply();
    }
}
