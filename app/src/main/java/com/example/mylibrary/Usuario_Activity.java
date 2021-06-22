package com.example.mylibrary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mylibrary.databinding.ActivityUsuarioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Usuario_Activity extends AppCompatActivity {

    private ActivityUsuarioBinding binding;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionBar = getSupportActionBar();
        actionBar.setTitle("LogIn");

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(Usuario_Activity.this, Login_Activity.class));
            finish();
        }else {
            String email = firebaseUser.getEmail();
            binding.emailTxt.setText(email);

        }
    }
}