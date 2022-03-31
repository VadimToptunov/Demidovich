package com.demidovich;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_pass;
    Button btn_generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_pass = (TextView)findViewById(R.id.tv_pass);
        btn_generate = (Button)findViewById(R.id.btn_generate);

        btn_generate.setOnClickListener(v -> {
            CreatePass createPassword = new CreatePass();
            tv_pass.setText(createPassword.createRandomPassword());
        });
    }
}