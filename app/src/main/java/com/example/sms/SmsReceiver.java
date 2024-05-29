package com.example.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmsReceiver extends BroadcastReceiver {

    private static final Set<String> processedMessages = new HashSet<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String senderPhoneNumber;

        if (bundle != null) {
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        senderPhoneNumber = msgs[i].getOriginatingAddress();
                        String messageBody = msgs[i].getMessageBody();
                        String messageId = senderPhoneNumber + ":" + messageBody;

                        if (!processedMessages.contains(messageId)) {
                            processedMessages.add(messageId);
                            if (isContactSelected(context, senderPhoneNumber)) {
                                sendAutoReply(context, senderPhoneNumber);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, "SMS reception failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private boolean isContactSelected(Context context, String phoneNumber) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("selectedContacts", Context.MODE_PRIVATE);
        Map<String, ?> contacts = sharedPreferences.getAll();
        return contacts.containsKey(phoneNumber);
    }

    private void sendAutoReply(Context context, String phoneNumber) {
        SharedPreferences spamPreferences = context.getSharedPreferences("autoResponsePreferences", Context.MODE_PRIVATE);
        String message = spamPreferences.getString("autoResponse", null);

        if (message != null) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "Auto-reply sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No auto-reply message selected", Toast.LENGTH_SHORT).show();
        }
    }
}
