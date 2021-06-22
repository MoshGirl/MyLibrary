package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private String email = "", senha = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        checkUser();

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Carregando, aguarde");
        progressDialog.setMessage("Logando em sua conta...");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDados2();

            }
        });

        binding.btCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login_Activity.this, Cadastro_Activity.class);
                startActivity(it);
            }
        });

    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(Login_Activity.this, Usuario_Activity.class));
            finish();
        }
    }

    private void validarDados2() {
        email = binding.edtEmail.getText().toString().trim();
        senha = binding.edtSenha.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtEmail.setError("E=mail com formato Inválido!");
        }
        else if(TextUtils.isEmpty(senha)){
            binding.edtSenha.setError("Insira uma senha");
        }
        else {
            firebaseLogin();
        }

    }

    private void firebaseLogin() {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                Toast.makeText(Login_Activity.this, "Logado com Sucesso em \n" +email, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login_Activity.this, Usuario_Activity.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}