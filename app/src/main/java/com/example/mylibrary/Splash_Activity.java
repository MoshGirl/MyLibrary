package com.example.mylibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Activity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Esconde a barra
        getSupportActionBar().hide();

        //Adiciona um tempo de carregamento, para ir a tela inicial
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(Splash_Activity.this, Login_Activity.class);
                startActivity(it);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}