package com.example.pfi_tesst;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Criar_vaga_2 extends AppCompatActivity{
    private Button add_crianca, concluir;
    private EditText idade, necessidade;
    private ArrayList<Integer> idades;
    private ArrayList<String> necessidades;

    private Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_2);
        add_crianca = findViewById(R.id.add_crianca);
        concluir= findViewById(R.id.concluir);
        idade = findViewById(R.id.idade);
        necessidade= findViewById(R.id.necessidade);
        extras = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Criação de Vaga");
        idades = new ArrayList<Integer>();
        necessidades = new ArrayList<String>();

        add_crianca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idades.add(Integer.parseInt(idade.getText().toString()));
                necessidades.add(necessidade.getText().toString());
                limpar();
            }
        });

        concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idades.isEmpty() || necessidades.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Você deve informar os dados corretamente!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null) {//ta criando uma colecao vaga com o nome do id do usario logado dentro da colecao Vaga_emprego
                    String userId = auth.getCurrentUser().getUid();
                    DocumentReference docRef = db.collection("Contratantes").document(userId);
                    CollectionReference vaga = db.collection("Vaga_emprego").document(userId).collection("vagas");

                    Vaga_emprego vaga_emprego = new Vaga_emprego();
                    vaga_emprego.setId(userId);
                    vaga_emprego.setQtd(idades.size());
                    vaga_emprego.setIdade_criancas(idades);
                    vaga_emprego.setNecessidades_criancas(necessidades);
                    vaga_emprego.setPreco((float) extras.get("preco"));
                    vaga_emprego.setMensal((Boolean) extras.get("mensal"));
                    vaga_emprego.setDisponibilidade((HashMap<String, HashMap<String, Boolean>>) extras.get("dias"));
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                                vaga_emprego.setEndereco(contratante.getEndereco());
                                vaga_emprego.setNome(contratante.getNome());
                                Log.i("ENTROU: ","SUCESSO");
                                vaga.add(vaga_emprego)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "Vaga de emprego adicionada com sucesso! ID do documento: " + documentReference.getId());
                                                abrir_tela_geral(Home_empregador.class);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Erro ao adicionar a vaga de emprego", e);
                                            }
                                        });
                                contratante.setVaga_emprego(vaga_emprego);
                                Log.i("CONTRATANTE: ",contratante.getId());
                                db.collection("Contratantes").document(contratante.getId()).set(contratante);
                            } else {
                                Log.i("DOCUMENTO: ","NÃO EXISTE");
                            }
                        }
                    });


                } else {
                    Log.e(TAG, "Nenhum usuário logado.");
                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }
    private void limpar(){
        idade.setText(""); necessidade.setText("");
    }

    private void abrir_tela_geral(Class<?> activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
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

    private int[] converterIntegers(ArrayList<Integer> integers) {
        int[] result = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            result[i] = integers.get(i);
        }
        return result;
    }

    private String[] converterStrings(ArrayList<String> array) {
        String[] res = new String[array.size()];
        for (int i =0;i<array.size();i++){
            res[i]=array.get(i);
        }
        return res;
    }

}
