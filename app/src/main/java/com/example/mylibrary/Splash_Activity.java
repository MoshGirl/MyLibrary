package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Activity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        //Esconde a barra
        getSupportActionBar().hide();

        //Adiciona um tempo de carregamento, para ir a tela inicial
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
                Intent it = new Intent(Splash_Activity.this, Login_Activity.class);
                startActivity(it);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(Splash_Activity.this, Usuario_Activity.class));
            finish();
        }else{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userType = ""+snapshot.child("userType").getValue();
                            if(userType.equals("user")){

                                startActivity(new Intent(Splash_Activity.this, Usuario_Activity.class));
                                finish();
                            }
                            else if(userType.equals("admin")){
                                startActivity(new Intent(Splash_Activity.this, Usuario_Activity.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}