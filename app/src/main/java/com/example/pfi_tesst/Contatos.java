package com.example.pfi_tesst;

/*Arrumar o titulo da interface e possivelmente uma serachView*/

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Contatos extends AppCompatActivity{
    private RecyclerView recyclerView;
    private List<Contratante> contratantes;

    private List<Cuidador_criancas> cuidadores;
    private EditText search;
    private int type;

    private String lastSearchTerm = "";


    private Handler handler = new Handler();
    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {

            String searchTerm = lastSearchTerm;
            if (!searchTerm.isEmpty()) {
                carregar_search(searchTerm);
            } else {
                carregar_ctt();
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String searchTerm = charSequence.toString();

            if (!searchTerm.equals(lastSearchTerm)) {
                lastSearchTerm = searchTerm;
                handler.removeCallbacks(searchRunnable);

                handler.postDelayed(searchRunnable, 500); // 500 milissegundos de atraso
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CONTATOS");
        recyclerView = findViewById(R.id.recycler);
        search = findViewById(R.id.home_search);
        RecyclerView.ItemDecoration divisor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divisor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(),Chat.class);
                if (type==0){
                    Contratante contratante = contratantes.get(position);
                    intent.putExtra("from_id",contratante.getId());
                    intent.putExtra("ft",contratante.getFoto_perfil());
                    intent.putExtra("type",type);
                }else{
                    Cuidador_criancas cuidador_criancas = cuidadores.get(position);
                    intent.putExtra("from_id",cuidador_criancas.getId());
                    intent.putExtra("ft",cuidador_criancas.getFoto_perfil());
                    intent.putExtra("type",type);
                }
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // fazer o treco de excluir ctt
            }
        }));


        search.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void carregar_ctt(){
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    db.collection("Contratantes").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()){
                Log.i("CONTRATANTE: "," TIPO CONTRATANTE");
                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                cuidadores = contratante.getAmigos();
                type=1;
                Ctt_adpater adpater = new Ctt_adpater(cuidadores,null,getApplicationContext(),type);
                recyclerView.setAdapter(adpater);
            }else{
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Log.i("TIPO CUIDADOR:"," CUDIDADOR");
                            Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                            contratantes = cuidador.getAmigos();
                            type=0;
                            Ctt_adpater adpater = new Ctt_adpater(null,contratantes,getApplicationContext(),type);
                            recyclerView.setAdapter(adpater);
                        }
                    }
                });
            }
        }
    });
}

private void carregar_search(String searchTerm){
        if (contratantes==null && cuidadores==null)return;
    if(contratantes!=null) contratantes.clear();
    if(cuidadores!=null)cuidadores.clear();

    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    db.collection("Contratantes").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()){
                Contratante contratante = documentSnapshot.toObject(Contratante.class);
                type=1;
                for (Cuidador_criancas cuidador_criancas: contratante.getAmigos()){
                    if (cuidador_criancas.getNome().toUpperCase().contains(searchTerm.toUpperCase())){
                        cuidadores.add(cuidador_criancas);
                    }
                }
                Ctt_adpater adpater = new Ctt_adpater(cuidadores,null,getApplicationContext(),type);
                recyclerView.setAdapter(adpater);
            }else{
                db.collection("Cuidadores").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Cuidador_criancas cuidador = documentSnapshot.toObject(Cuidador_criancas.class);
                        type=0;
                        for (Contratante contratante: cuidador.getAmigos()){
                            if (contratante.getNome().toUpperCase().contains(searchTerm.toUpperCase()))contratantes.add(contratante);
                        }
                        Ctt_adpater adpater = new Ctt_adpater(null,contratantes,getApplicationContext(),type);
                        recyclerView.setAdapter(adpater);
                    }
                });
            }
        }
    });

    if (cuidadores==null && contratantes==null){
        Ctt_adpater adpater = new Ctt_adpater(null,null,getApplicationContext(),type);
        recyclerView.setAdapter(adpater);
    }
}


    @Override
    protected void onResume() {
        super.onResume();
        carregar_ctt();
    }
}
