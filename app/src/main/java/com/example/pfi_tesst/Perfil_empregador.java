package com.example.pfi_tesst;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Perfil_empregador extends AppCompatActivity {
    private Button btn,editar_perfil,btn_excluir;
    private TextView endereco_user, idade_user, descricao_user,nome_user;

    private FirebaseFirestore db;

    private ImageView foto_perfil;
    private Bundle extras;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_empregador);
        btn = findViewById(R.id.enviar);
        endereco_user = findViewById(R.id.endereco);
        idade_user = findViewById(R.id.idade);
        descricao_user = findViewById(R.id.descricao_perfil);
        foto_perfil = findViewById(R.id.foto_perfil);
        nome_user= findViewById(R.id.nome_user);
        editar_perfil = findViewById(R.id.editar_perfil);
        db = FirebaseFirestore.getInstance();
        extras= getIntent().getExtras();
        btn_excluir=findViewById(R.id.btn_excluir);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Perfil");

        recuperar_dados();


        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Editar_perfil_1.class);
            }
        });

        btn_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference doc = db.collection("Contratantes").document(user_id);
                doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                abrirTela(MainActivity.class);
                                Toast.makeText(getApplicationContext(),"Conta excluida com sucesso!",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }
    private void abrirTela(Class<?> tela){
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }

    private void recuperar_dados() {
        String user_id = (String) extras.get("id");

        DocumentReference docRef = db.collection("Contratantes").document(user_id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Contratante contratante = documentSnapshot.toObject(Contratante.class);

                    String endereco = contratante.getEndereco();
                    //////////////////////////////////////////////////////////////////////

                    nome_user.setText(contratante.getNome());
                    idade_user.setText(String.valueOf(contratante.getIdade()));
                    descricao_user.setText(contratante.getDescricao());
                    endereco_user.setText(endereco);
                    Log.i("FOTO PERFIL: ",contratante.getFoto_perfil());

                    //////////////////////////////////////////////////////////////////////
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageReference = storage.getReference().child("imagens").child(user_id+".jpg");
                    Log.i("REFERENCIA: ",storageReference.toString());

                    Glide.with(getApplicationContext()).load(Uri.parse(contratante.getFoto_perfil())).into(foto_perfil);
                } else {
                    Log.i("DOCUMENTO: ","NÃO EXISTE");
                }

            }
        });




    }

    private String get_localizacao(Double latitude, Double longitude){
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String address = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String knownName = addresses.get(0).getFeatureName(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


}