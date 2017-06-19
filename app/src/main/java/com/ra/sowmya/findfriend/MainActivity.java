package com.ra.sowmya.findfriend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("login","");
        System.out.println("***************"+login);
        if(login.length()>0){

            //when user has his session running...jumps to Home page displaying Friends
            Intent intent = new Intent(this,Addfriends.class);
            startActivity(intent);
            finish();
        }
        else{

            //When user session is not running...returns login page
            Intent intent = new Intent(this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }
    }
}

