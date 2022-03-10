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
        db1 = new DB(this);
        loginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
        registerpage = (Button) findViewById(R.id.btn_registerpage);
        registerpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user1 = user.getText().toString();
                String pass1 = pass.getText().toString();
                String cpass1 = cpass.getText().toString();

                if (TextUtils.isEmpty(user1) || TextUtils.isEmpty(pass1) || TextUtils.isEmpty(cpass1)) {
                    Toast.makeText(registerActivity.this, "Please enter all fields", Toast.LENGTH_LONG).show();

                } else {
                    if (pass1.equals(cpass1)) {
                        Boolean checkuser = db1.checkUsername(user1);
                        if (checkuser == false) {
                            Boolean insert = db1.insertData(user1, pass1);
                            if (insert == true) {
                                Toast.makeText(registerActivity.this, "Registration Successfully", Toast.LENGTH_LONG).show();
                                Intent intent1 = new Intent(getApplicationContext(), loginActivity.class);
                                startActivity(intent1);
                            } else {
                                Toast.makeText(registerActivity.this, "Registeration Failed", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(registerActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(registerActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }
}


