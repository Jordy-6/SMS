package com.example.sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class ResponsesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ResponseRecyclerAdapter adapter;
    private ArrayList<String> responses;
    private EditText responseEditText;
    private Button responseButton;

    public ResponsesFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responses, container, false);
        recyclerView = view.findViewById(R.id.responses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        responses = new ArrayList<>(Arrays.asList("Je suis occupé",
                "Je te rappelle",
                "Salut",
                "Je suis en réunion",
                "Je suis en train de conduire",
                "Je suis en train de manger"));
        adapter = new ResponseRecyclerAdapter(getContext(), responses);
        recyclerView.setAdapter(adapter);

        responseEditText = view.findViewById(R.id.new_response_text);
        responseButton = view.findViewById(R.id.add_response_button);

        loadResponses();

        responseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = responseEditText.getText().toString();
                if (!response.isEmpty()) {
                    responses.add(response);
                    adapter.notifyDataSetChanged();
                    saveResponses();
                    responseEditText.setText("");
                }
            }
        });

        return view;
    }

    private void loadResponses() {
        // Load responses from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("responses", Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt("size", 0);
        for (int i = 0; i < size; i++) {
            responses.add(sharedPreferences.getString("response" + i, ""));
        }
        adapter.notifyDataSetChanged();
    }

    private void saveResponses() {
        // Save responses to SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("responses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("size", responses.size());
        for (int i = 0; i < responses.size(); i++) {
            editor.putString("response" + i, responses.get(i));
        }
        editor.apply();
    }
}
