package com.demidovich.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demidovich.R;
import com.demidovich.database.DatabaseHelper;

import java.util.ArrayList;

public class ListPasswordAdapter extends RecyclerView.Adapter<ListPasswordAdapter.ViewHolder>{
    private final DatabaseHelper databaseHelper;
    private final ArrayList<String> allDbData;

    public ListPasswordAdapter(Context context){
        databaseHelper = new DatabaseHelper(context);
        allDbData = databaseHelper.getAllData();
    }

    @NonNull
    @Override
    public ListPasswordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPasswordAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(allDbData.get(position));
    }

    @Override
    public int getItemCount() {
        return allDbData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_item_saved_pass_text);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
