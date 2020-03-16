package com.app.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edugain.R;

public class StartPage extends AppCompatActivity {

    private static int splashTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(StartPage.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        }, splashTime);
    }
}
