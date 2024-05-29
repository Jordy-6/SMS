package com.example.sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResponseRecyclerAdapter extends RecyclerView.Adapter<ResponseRecyclerAdapter.ResponseViewHolder>{

    private ArrayList<String> responses;
    private int autoResponseCount = 0;
    private int spamResponseCount = 0;

    private Context context;

    public ResponseRecyclerAdapter(Context context, ArrayList<String> responses) {
        this.responses = responses;
        this.context = context;
    }

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.response, parent, false);

        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        String response = responses.get(position);
        holder.responseText.setText(response);
        holder.autoResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.autoResponse.isChecked()) {
                    if (autoResponseCount >= 1) {
                        holder.autoResponse.setChecked(false);
                    } else {
                        autoResponseCount++;
                        saveAutoResponse(response);
                    }
                } else {
                    autoResponseCount--;
                    saveAutoResponse(null);
                }
            }
        });

        holder.spamResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.spamResponse.isChecked()) {
                    if (spamResponseCount >= 1) {
                        holder.spamResponse.setChecked(false);
                    } else {
                        spamResponseCount++;
                        saveSelectedSpam(response);
                    }
                } else {
                    spamResponseCount--;
                    saveSelectedSpam(null);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responses.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    private void saveSelectedSpam(String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("spamPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedSpam", message);
        editor.apply();
    }

    private void saveAutoResponse(String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("autoResponsePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoResponse", message);
        editor.apply();
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder {
        private final TextView responseText;
        private final CheckBox autoResponse, spamResponse;

        private final Button deleteButton;

        public ResponseViewHolder(View view) {
            super(view);
            responseText = view.findViewById(R.id.response_text);
            autoResponse = view.findViewById(R.id.auto_reply_checkbox);
            spamResponse = view.findViewById(R.id.spam_checkbox);
            deleteButton = view.findViewById(R.id.delete_response);
        }
    }
}
