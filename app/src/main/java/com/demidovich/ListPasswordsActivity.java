package com.demidovich;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.demidovich.adapters.ListPasswordAdapter;
import com.demidovich.helpers.ListItem;

public class ListPasswordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passwords);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListPasswordAdapter adapter = new ListPasswordAdapter(this, new ListItem()
                .getAllPasswords(this));
        recyclerView.setAdapter(adapter);
        //TODO: Fix displaying removing the passwords
    }
}