package com.example.pfi_tesst;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Perfil_cuidador_propio extends AppCompatActivity{

    private RadioGroup periodos;
    private TextView text_titulo;
    private CheckBox[] list_dias;private String[] titulos;
    private Bundle extras;
    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();
    private ImageView seta_direita, seta_esquerda;private int cont_image;
    private Button editar_perfil,excluir_perfil;
    private TextView endereco_user, idade_user, descricao_user,nome_user, preco_diario,preco_mensal;private ImageView foto_perfil;
    private FirebaseFirestore db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_cuidador_propio);
        text_titulo = findViewById(R.id.text_titulo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Perfil");
        editar_perfil = findViewById(R.id.editar_perfil);
        excluir_perfil = findViewById(R.id.btn_excluir);
        endereco_user = findViewById(R.id.endereco);
        idade_user = findViewById(R.id.idade);
        descricao_user = findViewById(R.id.descricao_perfil);
        foto_perfil = findViewById(R.id.foto_perfil);
        nome_user= findViewById(R.id.nome_user);
        preco_diario = findViewById(R.id.preco_diario); preco_mensal = findViewById(R.id.preco_mensal);

        titulos = new String[]{"Manhã", "Noite", "Tarde"};
        cont_image = 0;




        extras = getIntent().getExtras();

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4), findViewById(R.id.checkBox5), findViewById(R.id.checkBox6), findViewById(R.id.checkBox7)
        };

        db = FirebaseFirestore.getInstance();

        recuperar_dados();
        Log.i("ID: ",(String) extras.get("id"));
        db.collection("Cuidadores").document((String) extras.get("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i("DISPONIBILIDADE: ",disponibilidade.toString()+".");
                Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                disponibilidade = cuidador.getDisponibilidade();Log.i("DISPONIBILIDADE2: ",disponibilidade.toString());
                definir_diponibilidade();
            }
        });

        for (int i = 0; i < 7; i++) {
            list_dias[i].setClickable(false);
        }


        seta_direita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image == 2) {
                    cont_image = 0;
                } else cont_image++;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        seta_esquerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image == 0) {
                    cont_image = 2;
                } else cont_image--;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(Editar_perfil_1.class);
            }
        });

        excluir_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference doc = db.collection("Cuidadores").document(user_id);
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
    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }

    private void recuperar_dados() {
        String user_id = (String) extras.get("id");

        DocumentReference docRef = db.collection("Cuidadores").document(user_id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);

                    String endereco = cuidador.getEndereco();
                    //////////////////////////////////////////////////////////////////////

                    nome_user.setText(cuidador.getNome());
                    idade_user.setText(String.valueOf(cuidador.getIdade()));
                    descricao_user.setText(cuidador.getDescricao());
                    endereco_user.setText(endereco);
                    preco_diario.setText("R$ "+String.valueOf(cuidador.getPreco_diario()));
                    preco_mensal.setText("R$ "+String.valueOf(cuidador.getPreco_mensal()));

                    //////////////////////////////////////////////////////////////////////
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageReference = storage.getReference().child("imagens").child(user_id+".jpg");
                    Log.i("REFERENCIA: ",storageReference.toString());

                    Glide.with(getApplicationContext()).load(Uri.parse(cuidador.getFoto_perfil())).into(foto_perfil);
                } else {
                    Log.i("DOCUMENTO: ","NÃO EXISTE");
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
    private void definir_diponibilidade() {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        HashMap<String, Boolean> bah = disponibilidade.get(text_titulo.getText().toString());
        Log.i("BAH: ",bah.toString()+".");
        if (bah!=null){
            for (int i = 0; i < 7; i++) {
                list_dias[i].setChecked(bah.get(dias_semana[i]));
            }
        }
    }

    private void atualizar_disponibilidade(boolean b, int i) {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        HashMap<String, Boolean> temporario = new HashMap<>();
        int cont = 0;
        for (String dia : dias_semana) {
            boolean vec = list_dias[cont].isChecked();
            temporario.put(dia, vec);
            cont++;
        }
        disponibilidade.put(text_titulo.getText().toString(), temporario);
    }
}
