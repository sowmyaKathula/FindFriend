package com.ra.sowmya.findfriend;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText pass,phone;
    private Button signin;
    private TextView signup_i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pass=(EditText) findViewById(R.id.password);
        phone=(EditText) findViewById(R.id.phone);
        signin=(Button) findViewById(R.id.signin);
        signup_i =(TextView) findViewById(R.id.signup_intent);

        //Calling the Registration Activity....
        signup_i.setOnClickListener(e->{
            Intent it= new Intent(this, Registration.class);
            startActivity(it);
            finish();
        });

        //on Login
        signin.setOnClickListener(e->{
            ServerDB.LOG_USER(
                    pass.getText().toString(),
                    phone.getText().toString(),
                    this
            );
        });
    }
}
