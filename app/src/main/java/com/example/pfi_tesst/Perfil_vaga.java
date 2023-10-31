package com.example.pfi_tesst;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Perfil_vaga extends AppCompatActivity{
    private Bundle extras;
    private FirebaseFirestore db;

    private ImageView ft_perfil;
    private TextView nome, salario, idades, necessidades;
    private TextView text_titulo;
    private ImageView seta_direita, seta_esquerda;

    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();
    private CheckBox[] list_dias;private String[] titulos;
    private Button solicitacao;
    private int cont_image;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_vaga);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vaga de emprego");
        extras = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        text_titulo = findViewById(R.id.text_titulo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);

        ft_perfil = findViewById(R.id.foto_perfil);
        nome = findViewById(R.id.nome_user);
        salario = findViewById(R.id.preco_diario);
        idades = findViewById(R.id.idades);
        necessidades = findViewById(R.id.necessidades_especiais);

        solicitacao = findViewById(R.id.solicitacao);

        titulos = new String[]{"Manhã", "Noite", "Tarde"};
        cont_image = 0;

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4), findViewById(R.id.checkBox5), findViewById(R.id.checkBox6), findViewById(R.id.checkBox7)
        };


        for (int i = 0; i < 7; i++) {
            list_dias[i].setClickable(false);
        }

        recuperar_dados();


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


        solicitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap<String,Object> bah = new HashMap<>();
                bah.put("toId",user_id);
                db.collection("Notificacoes").document((String) extras.get("id")).collection("to_ids").document(user_id).set(bah);
                abrirTela(Home_cuidador.class);
                Toast.makeText(getApplicationContext(),"Solicitação enviada com sucesso",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        startActivity(intent);
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

    private void recuperar_dados() {
        String user_id = (String) extras.get("id");

        CollectionReference docRef = db.collection("Vaga_emprego").document(user_id).collection("vagas");

        docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Vaga_emprego vaga_emprego = doc.toObject(Vaga_emprego.class);

                salario.setText(String.valueOf(vaga_emprego.getPreco()));
                StringBuilder idades_criancas = new StringBuilder();
                for (int i =0;i<vaga_emprego.getIdade_criancas().size();i++){
                    if (i!=vaga_emprego.getIdade_criancas().size()-1){
                        idades_criancas.append(String.valueOf(vaga_emprego.getIdade_criancas().get(i) + ", "));
                    }else{
                        idades_criancas.append(String.valueOf(vaga_emprego.getIdade_criancas().get(i) + "."));
                    }
                }
                idades.setText(idades_criancas);
                StringBuilder necessidadess = new StringBuilder();
                for (int i =0;i<vaga_emprego.getNecessidades_criancas().size();i++){
                    if (i!=vaga_emprego.getNecessidades_criancas().size()-1){
                        necessidadess.append(String.valueOf(vaga_emprego.getNecessidades_criancas().get(i) + ", "));
                    }else{
                        necessidadess.append(String.valueOf(vaga_emprego.getNecessidades_criancas().get(i) + "."));
                    }
                }
                necessidades.setText(necessidadess);

                disponibilidade = vaga_emprego.getDisponibilidade();
                definir_diponibilidade();

            }
        });

        db.collection("Contratantes").document((String) extras.get("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                nome.setText(contratante.getNome());
                Glide.with(getApplicationContext()).load(contratante.getFoto_perfil()).into(ft_perfil);
            }
        });
    }
}
