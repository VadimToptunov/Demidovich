package com.demidovich;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.demidovich.helpers.CreatePass;
import com.demidovich.database.DatabaseHelper;

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

        tv_pass = (TextView)findViewById(R.id.tv_pass);
        btn_generate = (Button)findViewById(R.id.btn_generate);
        btn_save = (Button)findViewById(R.id.btn_save);
        go_to_saved = (ImageButton)findViewById(R.id.btn_saved_passwords);

        btn_generate.setOnClickListener(v -> {
            CreatePass createPassword = new CreatePass();
            tv_pass.setText(createPassword.createRandomPassword());
            btn_save.setEnabled(true);
        });

        btn_save.setOnClickListener(view ->{
            databaseHelper = new DatabaseHelper(this);
            databaseHelper.saveToDb(tv_pass.getText().toString());
            Toast.makeText(this, "Пароль збережено", Toast.LENGTH_LONG).show();
        });

        if (tv_pass.getText().toString().equals("Пароль")){
            btn_save.setEnabled(false);
        }

        go_to_saved.setOnClickListener(act ->{
            Intent intent = new Intent(this, ListPasswordsActivity.class);
            startActivity(intent);
        });
    }
}