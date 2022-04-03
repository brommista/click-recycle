package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class registerActivity extends AppCompatActivity {
    Button loginpage;
    Button registerpage;
    TextView user, pass, cpass;
    DB db1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user = (EditText) findViewById(R.id.txt_username);
        pass = (EditText) findViewById(R.id.txt_password);
        cpass = (EditText) findViewById(R.id.txt_cpassword);
        loginpage = (Button) findViewById(R.id.btn_loginpage);

        //Initialize an object of DB class
        db1 = new DB(this);


        registerpage = (Button) findViewById(R.id.btn_registerpage);
        registerpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user1 = user.getText().toString();
                String pass1 = pass.getText().toString();
                String cpass1 = cpass.getText().toString();

                //To check if user enters all values
                if (TextUtils.isEmpty(user1) || TextUtils.isEmpty(pass1) || TextUtils.isEmpty(cpass1)) {
                    Toast.makeText(registerActivity.this, "All fields are required", Toast.LENGTH_LONG).show();

                } else {
                    //To register user has to enter password twice. This is check if both password match
                    if (pass1.equals(cpass1)) {
                        //To check if username already exists in table as username is primary key
                        Boolean checkuser = db1.checkUsername(user1);
                        if (checkuser == false) {
                            //Once above conditions are met, new username and password is inserted in database table
                            Boolean insert = db1.insertData(user1, pass1);
                            if (insert == true) {
                                Toast.makeText(registerActivity.this, "Registration Successfully", Toast.LENGTH_LONG).show();
                                //To start logicActivity
                                Intent intent1 = new Intent(getApplicationContext(), loginActivity.class);
                                startActivity(intent1);
                            }


                            else {
                                Toast.makeText(registerActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                        // If username exists in table
                        else {
                            Toast.makeText(registerActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                        }
                    }
                    // If passwords do not match
                    else {
                        Toast.makeText(registerActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // In case user has login credentials, they can go to login page
        loginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }
}


