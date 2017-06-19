package com.ra.sowmya.findfriend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Registration extends AppCompatActivity {

    private Button signup;
    TextView login;
    private EditText username,password,email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        INIT
        username=(EditText) findViewById(R.id.name);
        password=(EditText) findViewById(R.id.password);
        email=(EditText) findViewById(R.id.email);
        phone=(EditText) findViewById(R.id.phone);
        signup=(Button) findViewById(R.id.signup);
        login = (TextView) findViewById(R.id.signin_intent);

        login.setOnClickListener(e->{
            Intent it= new Intent(this,Login.class);
            startActivity(it);
            finish();
        });

        signup.setOnClickListener(e->{
            ServerDB.REG_USER(
                    username.getText().toString().toUpperCase(),
                    password.getText().toString(),
                    email.getText().toString().toLowerCase(),
                    phone.getText().toString(),
                    this.getApplicationContext()
            );
            //Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
            finish();
        });


    }
}
