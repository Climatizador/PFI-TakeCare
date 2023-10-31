package com.example.pfi_tesst;

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
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Criar_vaga extends AppCompatActivity{

    private Button btn_concluir ;

    private RadioGroup periodos;

    /*private CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5 ,checkbox6, checkbox7;*/

    private CheckBox[] list_dias;

    private HashMap<String, HashMap<String, Boolean>> disponibilidade = new HashMap<>();

    private ImageView seta_direita, seta_esquerda;


    private int cont_image;
    private TextView text_titulo;
    private String[] titulos;

    private Set<Integer> demarcados;

    private EditText idade, necessidade, preco; private Switch mensal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga);
        periodos = findViewById(R.id.xunxo);
        seta_direita = findViewById(R.id.imageView2);
        seta_esquerda = findViewById(R.id.imageView5);
        btn_concluir = findViewById(R.id.concluir);
        text_titulo = findViewById(R.id.text_titulo);
        demarcados = new HashSet<>();
        preco = findViewById(R.id.preco);
        mensal = findViewById(R.id.switch1);
        titulos = new String[]{"Manhã", "Noite","Tarde"}; cont_image=0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Criação de Vaga");
        def_disponibilidade();

        list_dias = new CheckBox[]{findViewById(R.id.checkBox1), findViewById(R.id.checkBox2), findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4),findViewById(R.id.checkBox5),findViewById(R.id.checkBox6),findViewById(R.id.checkBox7)
        };

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

        for (int i =0; i<7;i++) {
            int finalI = i;
            list_dias[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    atualizar_disponibilidade(b,finalI);
                }
            });
        }


        btn_concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Criar_vaga_2.class);
                intent.putExtra("dias", disponibilidade);
                intent.putExtra("preco",Float.parseFloat(preco.getText().toString()));
                intent.putExtra("mensal",mensal.isChecked());
                startActivity(intent);
            }
        });
    }

    private void definir_diponibilidade(){
        String[] dias_semana = {"dom","seg","ter","qua","qui","sex","sab"};
        HashMap<String,Boolean> bah = disponibilidade.get(text_titulo.getText().toString());
        for (int i =0;i<7;i++){
            list_dias[i].setChecked(bah.get(dias_semana[i]));
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
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

    private void abrir_tela_geral(Class<?> activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }

}
