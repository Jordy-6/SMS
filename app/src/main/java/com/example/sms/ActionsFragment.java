package com.example.sms;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;

public class ActionsFragment extends Fragment {
    private BroadcastReceiver receiver;
    private Spinner spinner;
    private Button sendSpamButton;
    private Switch autoReplySwitch;

    public ActionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        spinner = view.findViewById(R.id.contact_spinner);
        sendSpamButton = view.findViewById(R.id.send_spam_button);
        autoReplySwitch = view.findViewById(R.id.auto_reply_switch);

        Log.d(TAG, "Creating ActionsFragment");

        // Register the BroadcastReceiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received CONTACTS_UPDATED broadcast");
                updateSpinner();
            }
        };

        IntentFilter filter = new IntentFilter("com.example.sms.CONTACTS_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(receiver, filter);
        }

        sendSpamButton.setOnClickListener(v -> sendSpam());

        autoReplySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getContext().startService(new Intent(getContext(), AutoReplyService.class));
            } else {
                getContext().stopService(new Intent(getContext(), AutoReplyService.class));
            }
        });

        // Initial spinner setup
        updateSpinner();

        return view;
    }

    private void updateSpinner() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("selectedContacts", 0);
        Map<String, ?> contacts = sharedPreferences.getAll();
        ArrayList<String> contactList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : contacts.entrySet()) {
            contactList.add((String) entry.getValue());
        }

        // Set up spinner with contacts
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, contactList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void sendSpam() {
        // Récupérer le nom du contact sélectionné dans le spinner
        String contactName = (String) spinner.getSelectedItem();

        // Récupérer tous les contacts enregistrés
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("selectedContacts", 0);
        Map<String, ?> contacts = sharedPreferences.getAll();

        // Trouver le numéro de téléphone du contact sélectionné
        String contactNumber = null;
        for (Map.Entry<String, ?> entry : contacts.entrySet()) {
            if (entry.getValue().equals(contactName)) {
                contactNumber = entry.getKey();
                break;
            }
        }

        // Vérifier si le numéro de téléphone a été trouvé
        if (contactNumber != null) {
            // Récupérer le message "spam" sélectionné
            SharedPreferences spamPreferences = getContext().getSharedPreferences("spamPreferences", Context.MODE_PRIVATE);
            String message = spamPreferences.getString("selectedSpam", null);

            // Vérifier si un message spam a été sélectionné
            if (message == null) {
                Toast.makeText(getContext(), "Aucun message spam sélectionné.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Envoyer le message "spam" au contact sélectionné
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactNumber, null, message, null, null);
        } else {
            // Afficher un message d'erreur si le numéro de téléphone n'a pas été trouvé
            Toast.makeText(getContext(), "Numéro de téléphone non trouvé pour le contact sélectionné.", Toast.LENGTH_SHORT).show();
        }
    }
}
