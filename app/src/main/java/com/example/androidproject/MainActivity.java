package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button loginh;
    Button registerh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginh = (Button) findViewById(R.id.btn_loginhome);
        loginh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
             Intent intent = new Intent(MainActivity.this,loginActivity.class);
             startActivity(intent);
            }
        });
        registerh = (Button) findViewById(R.id.btn_register);
        registerh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,registerActivity.class);
                startActivity(intent);
            }
        });
    }

}