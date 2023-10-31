package com.example.pfi_tesst;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Filtro extends AppCompatActivity{


    private TextView text_titulo;
    private Button concluir;

    private RadioGroup periodos;

    /*private CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5 ,checkbox6, checkbox7;*/

    private CheckBox[] list_dias;

    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();

    private ImageView seta_direita, seta_esquerda;


    private int cont_image;

    private String[] titulos;
    private Switch aSwitch;
    private Set<Integer> demarcados;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_filtro);
        text_titulo = findViewById(R.id.text_titulo);
        periodos = findViewById(R.id.xunxo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);
        concluir = findViewById(R.id.concluir);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filtragem");
        demarcados = new HashSet<>();
        aSwitch = findViewById(R.id.switch2);
        titulos = new String[]{"Manhã", "Noite", "Tarde"};
        cont_image = 0;

        def_disponibilidade();

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4), findViewById(R.id.checkBox5), findViewById(R.id.checkBox6), findViewById(R.id.checkBox7)
        };

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
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Contratantes").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Contratante contratante =documentSnapshot.toObject(Contratante.class);
                            contratante.setFiltro(disponibilidade);
                            contratante.setApenas_cidade(aSwitch.isChecked());
                            db.collection("Contratantes").document(user_id).set(contratante);
                            Toast.makeText(getApplicationContext(),"Filtragem definida com sucesso!",Toast.LENGTH_SHORT).show();
                            abrir_tela_geral(Home_empregador.class);
                        }else{
                            db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Cuidador_criancas cuidador_criancas =documentSnapshot.toObject(Cuidador_criancas.class);
                                    cuidador_criancas.setFiltro(disponibilidade);
                                    cuidador_criancas.setApenas_cidade(aSwitch.isChecked());
                                    db.collection("Cuidadores").document(user_id).set(cuidador_criancas);
                                    Toast.makeText(getApplicationContext(),"Filtragem definida com sucesso!",Toast.LENGTH_SHORT).show();
                                    abrir_tela_geral(Home_cuidador.class);
                                }
                            });
                        }
                    }
                });
            }
        });

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            list_dias[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    atualizar_disponibilidade(b, finalI);
                }
            });
        }

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
        for (int i = 0; i < 7; i++) {
            list_dias[i].setChecked(bah.get(dias_semana[i]));
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
        Log.i("DISPONIBILIDADE", String.valueOf(disponibilidade));
        Log.i("DEMARCADOS ", String.valueOf(demarcados));
    }


    private void def_disponibilidade() {
        String[] dias_semana = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        for (String periodo : new String[]{"Manhã", "Tarde", "Noite"}) {
            HashMap<String, Boolean> temporario = new HashMap<>();
            for (String dia : dias_semana) {
                temporario.put(dia, false);
            }
            disponibilidade.put(periodo, temporario);
        }

        Log.i("DISPONIBILIDADE", String.valueOf(disponibilidade));
    }

    private void abrir_tela_geral(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
