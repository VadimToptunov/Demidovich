package com.demidovich.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demidovich.R;
import com.demidovich.helpers.ListItem;

import java.util.ArrayList;

public class ListPasswordAdapter extends RecyclerView.Adapter<ListPasswordAdapter.ViewHolder>{
    private final ArrayList<String> allDbData;
    private Context context;

    public ListPasswordAdapter(Context context, ArrayList<String> dbData){
        this.allDbData = dbData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = allDbData.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return allDbData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageButton button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_item_saved_pass_text);
            button = (ImageButton) itemView.findViewById(R.id.list_item_btn_delete);
        }
    }
}
