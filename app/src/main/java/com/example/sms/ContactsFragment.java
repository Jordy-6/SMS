package com.example.sms;

import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private ArrayList<Contact> contacts;

    public ContactsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = view.findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, getContext());
        recyclerView.setAdapter(adapter);

        loadContacts();


        return view;
    }

    private void loadContacts() {
        Cursor cursor = getContext().getContentResolver().query(android.provider.ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));

            Cursor pCur = getContext().getContentResolver().query(
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                String phone = pCur.getString(pCur.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new Contact(name, phone));
            }
            pCur.close();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
