package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.mylibrary.databinding.ActivityPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Perfil_Activity extends AppCompatActivity {

    private ActivityPerfilBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String nome = "" + dataSnapshot.child("nome").getValue();
                    String email = "" + dataSnapshot.child("email").getValue();
                    String imagem = "" + dataSnapshot.child("profileImage").getValue();

                    binding.nomePerfiltxt.setText(nome);
                    binding.emailPerfiltxt.setText(email);
                    try {
                        Picasso.get().load(imagem).into(binding.avatar);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_add_photo).into(binding.avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}