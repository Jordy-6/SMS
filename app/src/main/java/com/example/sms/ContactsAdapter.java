package com.example.sms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private static final String TAG = "ContactsAdapter";
    private ArrayList<Contact> contacts;
    private Context context;

    public ContactsAdapter(ArrayList<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactNumber.setText(contact.getNumber());

        // Set the checkbox state based on SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("selectedContacts", Context.MODE_PRIVATE);
        holder.checkBox.setChecked(sharedPreferences.contains(contact.getNumber()));

        holder.checkBox.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (holder.checkBox.isChecked()) {
                // Add contact to SharedPreferences
                editor.putString(contact.getNumber(), contact.getName());
            } else {
                // Remove contact from SharedPreferences
                editor.remove(contact.getNumber());
            }
            editor.apply();

            Log.d(TAG, "Broadcasting CONTACTS_UPDATED");
            Intent intent = new Intent("com.example.sms.CONTACTS_UPDATED");
            context.sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactName;
        private final TextView contactNumber;
        private final CheckBox checkBox;

        public ContactViewHolder(View view) {
            super(view);
            contactName = view.findViewById(R.id.contact_name);
            contactNumber = view.findViewById(R.id.contact_number);
            checkBox = view.findViewById(R.id.contact_checkbox1);
        }
    }
}
