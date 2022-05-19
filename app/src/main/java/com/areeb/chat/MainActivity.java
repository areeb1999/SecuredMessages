package com.areeb.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity { // Runs splash screen to initialize application

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("Message Folder", MODE_PRIVATE);
                String name = prefs.getString("uid", "No name defined");
                if(name.isEmpty()){ // Checks if logged in
                    Intent i = new Intent(MainActivity.this, LoginScreen.class);
                    startActivity(i);
                    finish();
                }else { // if logged in go to homepage
                    Intent i = new Intent(MainActivity.this, HomeScreen.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);





    }
}