package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.perfil){

        }

        if(item.getItemId() == R.id.config){

        }

        if(item.getItemId() == R.id.about){
            startActivity(new Intent(Usuario_Activity.this, About_Activity.class));
        }

        if(item.getItemId() == R.id.exit){
            firebaseAuth.signOut();
            checkUser();
        }
        return super.onOptionsItemSelected(item);
    }
}