package com.personal.cashflow.servics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.personal.cashflow.room.TransactionModel;
import com.personal.cashflow.room.TransactionRepository;
import com.personal.cashflow.utils.SmsParser;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            for (SmsMessage sms : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String msgBody = sms.getMessageBody();
                TransactionModel txn = SmsParser.parseMessage(msgBody);
                Log.d("SMS_RECEIVED", "Message: " + msgBody);
                Toast.makeText(context, "SMS received", Toast.LENGTH_SHORT).show();

                if (txn != null) {
                    // Save to Room DB
                    Log.d("SMS_RECEIVED", "====================================================================");
                    Log.d("SMS_RECEIVED", "Message: " + msgBody);
                    Log.d("SMS_RECEIVED", "====================================================================");
                    Toast.makeText(context, "SMS received", Toast.LENGTH_SHORT).show();

                    TransactionRepository.getInstance(context).insertTransaction(txn);
                }
            }
        }
    }
}

