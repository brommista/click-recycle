package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class registerActivity extends AppCompatActivity {
    Button loginpage;
    Button registerpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginpage = (Button) findViewById(R.id.btn_loginpage);
        loginpage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(registerActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
        registerpage = (Button) findViewById(R.id.btn_registerpage);
        registerpage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });
    }
}