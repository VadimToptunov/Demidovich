package com.demidovich;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.demidovich.adapters.ListPasswordAdapter;
import com.demidovich.database.DatabaseHelper;
import com.demidovich.helpers.ListItem;

public class ListPasswordsActivity extends AppCompatActivity {

    private ImageButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passwords);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListPasswordAdapter adapter = new ListPasswordAdapter(this, new ListItem().getAllPasswords(this));
        recyclerView.setAdapter(adapter);

//        deleteButton = (ImageButton)findViewById(R.id.list_item_btn_delete);

//        deleteButton.setOnClickListener(view -> {
//            deletePassword();
//        });



    }

//    private void deletePassword(){
//        DatabaseHelper databaseHelper = new DatabaseHelper(this);
//        databaseHelper.onDelete();//ToDo: Make it delete only one password;
//    }
}