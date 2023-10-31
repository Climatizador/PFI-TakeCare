package com.example.pfi_tesst;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Editar_perfil_3 extends AppCompatActivity {
    private TextView text_titulo;
    private EditText preco_diario, preco_mensal;
    private Bundle extras;

    private Button btn_cadastrar;

    private RadioGroup periodos;

    /*private CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5 ,checkbox6, checkbox7;*/

    private CheckBox[] list_dias;

    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();

    private ImageView seta_direita, seta_esquerda;


    private int cont_image;

    private String[] titulos;
    private FirebaseFirestore db;
    private Set<Integer> demarcados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_3);
        text_titulo = findViewById(R.id.text_titulo);
        preco_diario = findViewById(R.id.cadastro_preco_diario);
        preco_mensal = findViewById(R.id.cadastro_preco_mensal);
        periodos = findViewById(R.id.xunxo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);
        btn_cadastrar = findViewById(R.id.cadastro_continuar);
        getSupportActionBar().setTitle("Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();

        demarcados = new HashSet<>();

        titulos = new String[]{"Manhã", "Noite","Tarde"}; cont_image=0;

        extras = getIntent().getExtras();

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4),findViewById(R.id.checkBox5),findViewById(R.id.checkBox6),findViewById(R.id.checkBox7)
        };
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i("DISPONIBILIDADE: ",disponibilidade.toString()+".");
                Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                disponibilidade = cuidador.getDisponibilidade();
                preco_mensal.setText(String.valueOf(cuidador.getPreco_mensal()));
                preco_diario.setText(String.valueOf(cuidador.getPreco_diario()));
                definir_diponibilidade();
            }
        });

        seta_direita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image==2){
                    cont_image=0;
                }else cont_image++;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        seta_esquerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cont_image==0){
                    cont_image=2;
                }else cont_image--;
                text_titulo.setText(titulos[cont_image]);
                definir_diponibilidade();
            }
        });

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                concluir();
            }
        });

        for (int i =0; i<7;i++) {
            int finalI = i;
            list_dias[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    atualizar_disponibilidade(b,finalI);
                }
            });
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void concluir(){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("nome", extras.get("nome"));
            updates.put("email", extras.get("email"));
            updates.put("senha", extras.get("senha"));
            updates.put("descricao", extras.get("descricao"));
            updates.put("foto_perfil", extras.get("foto"));
            updates.put("disponibilidade",disponibilidade);
            updates.put("preco_diario",Float.parseFloat(preco_diario.getText().toString()));
            updates.put("preco_mensal",Float.parseFloat(preco_mensal.getText().toString()));
            db.collection("Cuidadores").document(user_id).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.i("EXTRAS: ",extras.toString());
                }
            });

            abrirTela(Home_cuidador.class);
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

    private void atualizar_disponibilidade(boolean b,int i ){
        String[] dias_semana = {"dom","seg","ter","qua","qui","sex","sab"};
        HashMap<String, Boolean> temporario = new HashMap<>();
        int cont=0;
        for (String dia : dias_semana) {
            boolean vec = list_dias[cont].isChecked();
            temporario.put(dia,vec);
            cont++;
        }
        disponibilidade.put(text_titulo.getText().toString(), temporario);
        Log.i("DISPONIBILIDADE", String.valueOf(disponibilidade));
        Log.i("DEMARCADOS ",String.valueOf(demarcados));
    }


    private void abrirTela(Class<?> tela){
        Intent intent = new Intent(this, tela);
        startActivity(intent);
    }

}