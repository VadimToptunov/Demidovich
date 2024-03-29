package com.demidovich;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.demidovich.database.DatabaseHelper;
import com.demidovich.helpers.CreatePass;

public class MainActivity extends AppCompatActivity {
    private TextView tv_pass;
    private Button btn_generate;
    private Button btn_save;
    private ImageButton go_to_saved;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_pass = findViewById(R.id.tv_pass);
        btn_generate = findViewById(R.id.btn_generate);
        btn_save = findViewById(R.id.btn_save);
        go_to_saved = findViewById(R.id.btn_saved_passwords);

        btn_generate.setOnClickListener(v -> {
            CreatePass createPassword = new CreatePass();
            tv_pass.setText(createPassword.createRandomPassword());
            btn_save.setEnabled(true);
        });

        btn_save.setOnClickListener(view -> {
            databaseHelper = new DatabaseHelper(this);
            databaseHelper.saveToDb(tv_pass.getText().toString());
            Toast.makeText(this, R.string.toast_text, Toast.LENGTH_LONG).show();
            btn_save.setEnabled(false);
        });

        if (tv_pass.getText().toString().equals(getResources().getString(R.string.pass_default))) {
            btn_save.setEnabled(false);
        }

        go_to_saved.setOnClickListener(act -> {
            Intent intent = new Intent(this, ListPasswordsActivity.class);
            startActivity(intent);
        });
    }
}