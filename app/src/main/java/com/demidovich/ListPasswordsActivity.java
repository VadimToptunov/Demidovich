package com.demidovich;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.demidovich.adapters.ListPasswordAdapter;
import com.demidovich.adapters.ListPasswordAdapter.ViewHolder;
import com.demidovich.database.DatabaseHelper;

public class ListPasswordsActivity extends AppCompatActivity {

    private ViewHolder listPasswordsAdapterViewHolder;
    private ImageButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passwords);
        RecyclerView recyclerView = findViewById(R.id.reciclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListPasswordAdapter(this)); //ToDo: Fix the crash

        deleteButton = (ImageButton)findViewById(R.id.list_item_btn_delete);

//        deleteButton.setOnClickListener(view -> {
//            deletePassword();
//        });



    }

    private void deletePassword(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.onDelete();//ToDo: Make it delete only one password;
    }
}