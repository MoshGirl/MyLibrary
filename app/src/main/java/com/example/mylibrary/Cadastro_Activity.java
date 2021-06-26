package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.mylibrary.databinding.ActivityCadastroBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Cadastro_Activity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private String email = "", confEmail = "", senha = "", confSenha = "", nome = "";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Carregando, aguarde");
        progressDialog.setMessage("Criando sua conta...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.fabCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDados();
            }
        });
    }

    private void validarDados() {
        email = binding.edtCadastroEmail.getText().toString().trim();
        confEmail = binding.edtConfirmaEmail.getText().toString().trim();
        senha = binding.edtCadastroSenha.getText().toString().trim();
        confSenha = binding.edtConfirmaSenha.getText().toString().trim();
        nome = binding.edtNome.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtCadastroEmail.setError("E=mail com formato Inválido!");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(confEmail).matches()){
            binding.edtConfirmaEmail.setError("E-mail com formato Inválido!");
        }
        else if(email.equals(confEmail) == false){
            binding.edtConfirmaEmail.setError("E-mail diferente!");
        }
        else if(TextUtils.isEmpty(senha)){
            binding.edtCadastroSenha.setError("Insira uma senha");
        }
        else if(TextUtils.isEmpty(confSenha)){
            binding.edtConfirmaSenha.setError("Insira uma senha");
        }
        else if(senha.equals(confSenha) == false){
            binding.edtConfirmaEmail.setError("Senhas diferentes!");
        }
        else if(senha.length() < 6){
            binding.edtCadastroSenha.setError("Senha abaixo de 6 caracteres!");
        }
        else if(TextUtils.isEmpty(nome)){
            binding.edtNome.setError("Digite seu Nome");
        }
        else {
            firebaseCadastro();
        }
    }


    private void firebaseCadastro() {
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                updateUserInfo();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Cadastro_Activity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Salvando usuário...");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        //setup dados para adicionar ao banco
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("nome", nome);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user"); //possiveis valores, user e admin
        hashMap.put("timeStamp", timestamp );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.dismiss();
                Toast.makeText(Cadastro_Activity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Cadastro_Activity.this, Usuario_Activity.class));
                finish();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Cadastro_Activity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}