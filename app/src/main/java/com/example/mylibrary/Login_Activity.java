package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Login_Activity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private String email = "", senha = "";
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Carregando, aguarde");
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
                startActivity(new Intent(Login_Activity.this, Cadastro_Activity.class));
            }
        });

        binding.txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoRecuperarSenha();
            }
        });

        binding.googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

    }

    private void dialogoRecuperarSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recupere sua senha");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailRec = new EditText(this);
        emailRec.setHint("Email");
        emailRec.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailRec.setMinEms(10);

        linearLayout.addView(emailRec);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recupere", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailRec.getText().toString().trim();
                recuperacao(email);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void recuperacao(String email) {
        progressDialog.setMessage("Enviando email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(Login_Activity.this, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Login_Activity.this, "Operação fracassou...", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        progressDialog.setMessage("Entrando...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUser();
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

    private void checkUser() {
        progressDialog.setMessage("Checando Usuario...");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        String userType = ""+snapshot.child("userType").getValue();
                        if(userType.equals("user")){
                            startActivity(new Intent(Login_Activity.this, Usuario_Activity.class));
                            finish();
                        }
                        else if(userType.equals("admin")){
                            startActivity(new Intent(Login_Activity.this, Usuario_Activity.class));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                long timestamp = System.currentTimeMillis();

                                String email = user.getEmail();
                                String uid = user.getUid();

                                //setup dados para adicionar ao banco
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("nome", "");
                                hashMap.put("profileImage", "");
                                hashMap.put("userType", "user"); //possiveis valores, user e admin
                                hashMap.put("timeStamp", timestamp );

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                            }

                            Toast.makeText(Login_Activity.this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Login_Activity.this, Usuario_Activity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login_Activity.this, "Falha...", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}