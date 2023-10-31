package com.example.pfi_tesst;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView enviar;
    private Serializable user;
    private EditText text;
    private FirebaseFirestore db;
    private String ft;
    private Cuidador_criancas cuidador_criancas;
    private List<Message> messageList;
    private Adpater_chat messageAdapter;
    private Bundle extras;
    private Contratante contratante;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        enviar = findViewById(R.id.imageView);
        text = findViewById(R.id.message);
        recyclerView = findViewById(R.id.recylcer);
        messageList = new ArrayList<Message>();
        db = FirebaseFirestore.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setPadding(0, 0, 0, 0);

        extras = getIntent().getExtras();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("Contratantes").document(user_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Contratante contratante1 = documentSnapshot.toObject(Contratante.class);
                    Log.i("CONTRATANTE: ", "contratante");
                    ft = contratante1.getFoto_perfil();
                    db.collection("Cuidadores").document((String) extras.get("from_id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Cuidador_criancas cuidador_criancas1 = documentSnapshot.toObject(Cuidador_criancas.class);
                            getSupportActionBar().setTitle(cuidador_criancas1.getNome());
                        }
                    });
                    Log.i("FOTO: ", ft);
                } else {
                    DocumentReference docRef = db.collection("Cuidadores").document(user_id);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Cuidador_criancas cuidador_criancas1 = documentSnapshot.toObject(Cuidador_criancas.class);
                                Log.i("CUIDADOR: ", "cuidador");
                                ft = cuidador_criancas1.getFoto_perfil();
                                db.collection("Contratantes").document((String) extras.get("from_id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Contratante contratante1 = documentSnapshot.toObject(Contratante.class);
                                        getSupportActionBar().setTitle(contratante1.getNome());
                                    }
                                });
                            } else {
                                Log.i("DOCUMENTO: ", "NÃO EXISTE");
                            }
                        }
                    });
                }
            }
        });


        db.collection("/conversas").document(user_id).collection((String) extras.get("from_id")).orderBy("timestamp", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i("RECUPERANDO: ", "DADOS");
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    messageList.add(documentSnapshot.toObject(Message.class));
                }
                messageAdapter = new Adpater_chat(messageList, getApplicationContext());
                recyclerView.setAdapter(messageAdapter);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("RECUPERANDO: ", "DADOS2");
            }
        });


        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(ft);
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go back to the previous activity
        finish();
        return true;
    }

    private void sendMessage(String ft) {
        String text1 = text.getText().toString();
        limpar();
        String fromId = FirebaseAuth.getInstance().getUid();
        String toId = (String) extras.get("from_id");

        long timestamp = System.currentTimeMillis();
        Message message = new Message();
        message.setText(text1);
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setFt(ft);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("/conversas").document(fromId).collection(toId).add(message);
        db.collection("/conversas").document(toId).collection(fromId).add(message);

        messageList.add(message);

        messageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void limpar() {
        text.setText("");
    }

}
