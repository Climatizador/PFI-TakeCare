package com.example.pfi_tesst;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.CompletableFuture;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Notificacao extends AppCompatActivity {
    private RecyclerView recyclerView;

    private int documentosProcessados;
    private Bundle extras;
    private Notificacao_adapter notificacao_adapter;
    private FirebaseFirestore db;
    private List<String> nomes;
    private List<String> endereco_fts = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notificações");

        extras = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler);
        nomes = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Notificacoes").document(user_id);

        Query query = documentReference.collection("to_ids");
        Log.d("DOC: ", documentReference.toString());
        Log.d("QUERY: ", query.toString());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<String> ids = new ArrayList<>();
                Log.i("QUERY DOC: ", querySnapshot.getDocuments().toString());
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    String toId = documentSnapshot.getString("toId");
                    ids.add(toId);
                }

                Log.i("IDS: ", ids.toString());
                recuperar_dados(user_id, ids);
            }
        });

    }


    private void recuperar_dados(String user_id, List<String> ids) {
        int totalDocumentos = ids.size();
        documentosProcessados = 0;

        for (String id : ids) {
            DocumentReference contratantesDocumentReference = db.collection("Contratantes").document(id);
            DocumentReference cuidadoresDocumentReference = db.collection("Cuidadores").document(id);

            contratantesDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Contratante contratante = documentSnapshot.toObject(Contratante.class);
                        nomes.add(contratante.getNome());
                        endereco_fts.add(contratante.getFoto_perfil());
                        documentosProcessados++;

                        if (documentosProcessados == totalDocumentos) {
                            notificacao_adapter = new Notificacao_adapter(user_id, nomes, getApplicationContext(), endereco_fts, ids);
                            recyclerView.setAdapter(notificacao_adapter);
                        }
                    } else {
                        cuidadoresDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Cuidador_criancas cuidador_criancas = documentSnapshot.toObject(Cuidador_criancas.class);
                                    nomes.add(cuidador_criancas.getNome());
                                    endereco_fts.add(cuidador_criancas.getFoto_perfil());

                                    documentosProcessados++;

                                    if (documentosProcessados == totalDocumentos) {
                                        notificacao_adapter = new Notificacao_adapter(user_id, nomes, getApplicationContext(), endereco_fts, ids);
                                        recyclerView.setAdapter(notificacao_adapter);
                                    }
                                }
                            }
                        });
                    }

                }
            });
        }


        if (documentosProcessados == totalDocumentos) {
            notificacao_adapter = new Notificacao_adapter(user_id, nomes, getApplicationContext(), endereco_fts, ids);
            recyclerView.setAdapter(notificacao_adapter);
        }
    }








    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
