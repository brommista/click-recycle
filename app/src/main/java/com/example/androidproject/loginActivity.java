package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {
    Button login;
    Button register;
    EditText username1, password1;
    DB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username1 = (EditText) findViewById(R.id.txt_username);
        password1 = (EditText) findViewById(R.id.txt_password);
        login = (Button) findViewById(R.id.btn_login);

        // Initialize an object of DB class
        db = new DB(this);




        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                String username = username1.getText().toString();
                String password = password1.getText().toString();

                //TextUtils is used to check if username and password string is empty.
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(loginActivity.this,"All fields are required", Toast.LENGTH_LONG).show();
                }

                else{
                    //
                    Boolean logincheck = db.checkLogin(username,password);
                    //If username and password exists, login is successful
                        if (logincheck==true) {
                            Toast.makeText(loginActivity.this,"Login successful", Toast.LENGTH_LONG).show();
                            //On successful login cameraActivity starts
                            Intent intent = new Intent(loginActivity.this,cameraActivity.class);
                            startActivity(intent);
                        }

                        //If db.checklogin returns false, User is not allowed to login
                        else{
                            Toast.makeText(loginActivity.this,"Login unsuccessful", Toast.LENGTH_LONG).show();
                        }
                }
            }
        });

        register = (Button) findViewById(R.id.btn_registerh);
        // If users does not have an account, they can go to registerActivity
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(loginActivity.this,registerActivity.class);
                startActivity(intent);
            }
        });
    }
}